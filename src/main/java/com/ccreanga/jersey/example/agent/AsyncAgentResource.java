package com.ccreanga.jersey.example.agent;

import com.ccreanga.jersey.example.domain.*;
import jersey.repackaged.com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.glassfish.jersey.client.rx.RxWebTarget;
import org.glassfish.jersey.client.rx.java8.RxCompletionStage;
import org.glassfish.jersey.client.rx.java8.RxCompletionStageInvoker;
import org.glassfish.jersey.server.Uri;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.GenericType;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Path("agent/async")
@Produces("application/json")
public class AsyncAgentResource {

    @Uri("remote/destination")
    private WebTarget destination;

    @Uri("remote/calculation/from/{from}/to/{to}")
    private WebTarget calculation;

    @Uri("remote/forecast/{destination}")
    private WebTarget forecast;

    private final ExecutorService executor;

    public AsyncAgentResource() {
        executor = new ScheduledThreadPoolExecutor(20,
                new ThreadFactoryBuilder().setNameFormat("jersey-rx-client-completion-%d").build());
    }

    @GET
    public void completion(@Suspended final AsyncResponse async) {
        final long time = System.nanoTime();
        final RxWebTarget<RxCompletionStageInvoker> rxDestination = RxCompletionStage.from(destination, executor);

        final Queue<String> errors = new ConcurrentLinkedQueue<>();

        CompletableFuture.completedFuture(new AgentResponse())
                .thenCombine(visited(rxDestination, errors), AgentResponse::visited)
                .thenCombine(recommended(rxDestination, errors), AgentResponse::recommended)
                .whenCompleteAsync((response, throwable) -> {
                    // Do something with errors.

                    response.setProcessingTime((System.nanoTime() - time) / 1000000);
                    async.resume(throwable == null ? response : throwable);
                });
    }

    private CompletionStage<List<Destination>> visited(final RxWebTarget<RxCompletionStageInvoker> rxDestination,
                                                       final Queue<String> errors) {
        return rxDestination.path("visited").request()
                .rx()
                .get(new GenericType<List<Destination>>() {
                })
                .exceptionally(throwable -> {
                    errors.offer("Visited: " + throwable.getMessage());
                    return Collections.emptyList();
                });
    }

    private CompletionStage<List<Recommendation>> recommended(final RxWebTarget<RxCompletionStageInvoker> rxDestination,
                                                              final Queue<String> errors) {
        // Recommended places.
        final CompletionStage<List<Destination>> recommended = rxDestination.path("recommended")
                .request()
                .rx()
                .get(new GenericType<List<Destination>>() {
                })
                .exceptionally(throwable -> {
                    errors.offer("Recommended: " + throwable.getMessage());
                    return Collections.emptyList();
                });

        return recommended.thenCompose(destinations -> {
            final RxWebTarget<RxCompletionStageInvoker> rxForecast = RxCompletionStage.from(forecast, executor);
            final RxWebTarget<RxCompletionStageInvoker> rxCalculation = RxCompletionStage.from(calculation, executor);

            List<CompletionStage<Recommendation>> recommendations = destinations.stream().map(destination -> {
                // For each destination, obtain a weather forecast ...
                final CompletionStage<Forecast> forecast = rxForecast.resolveTemplate("destination", destination.getDestination())
                        .request().rx().get(Forecast.class)
                        .exceptionally(throwable -> {
                            errors.offer("Forecast: " + throwable.getMessage());
                            return new Forecast(destination.getDestination(), "N/A");
                        });
                // ... and a price calculation
                final CompletionStage<Calculation> calculation = rxCalculation.resolveTemplate("from", "Moon")
                        .resolveTemplate("to", destination.getDestination())
                        .request().rx().get(Calculation.class)
                        .exceptionally(throwable -> {
                            errors.offer("Calculation: " + throwable.getMessage());
                            return new Calculation("Moon", destination.getDestination(), -1);
                        });

                //noinspection unchecked
                return CompletableFuture.completedFuture(new Recommendation(destination))
                        // Set forecast for recommended destination.
                        .thenCombine(forecast, Recommendation::forecast)
                        // Set calculation for recommended destination.
                        .thenCombine(calculation, Recommendation::calculation);
            }).collect(Collectors.toList());

            // Transform List<CompletionStage<Recommendation>> to CompletionStage<List<Recommendation>>
            return sequence(recommendations);
        });
    }

    private <T> CompletionStage<List<T>> sequence(final List<CompletionStage<T>> stages) {
        //noinspection SuspiciousToArrayCall
        final CompletableFuture<Void> done = CompletableFuture.allOf(stages.toArray(new CompletableFuture[stages.size()]));

        return done.thenApply(v -> stages.stream()
                .map(CompletionStage::toCompletableFuture)
                .map(CompletableFuture::join)
                .collect(Collectors.toList())
        );
    }

}