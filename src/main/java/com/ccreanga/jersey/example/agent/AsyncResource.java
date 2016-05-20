package com.ccreanga.jersey.example.agent;

import com.ccreanga.jersey.example.domain.CreditScore;
import com.ccreanga.jersey.example.domain.Destination;
import com.ccreanga.jersey.example.domain.Forecast;
import com.ccreanga.jersey.example.domain.Profile;
import jersey.repackaged.com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.glassfish.jersey.client.rx.RxWebTarget;
import org.glassfish.jersey.client.rx.java8.RxCompletionStage;
import org.glassfish.jersey.client.rx.java8.RxCompletionStageInvoker;
import org.glassfish.jersey.server.Uri;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.GenericType;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;

@Path("sync")
@Produces("application/json")
public class AsyncResource {

    @Uri("remote/score/{user}")
    private WebTarget score;

    @Uri("remote/profile/{user}")
    private WebTarget profile;

    @Uri("remote/rent/{user}")
    private WebTarget rent;

    private final ExecutorService executor;

    public AsyncResource() {
        executor = new ScheduledThreadPoolExecutor(20,
                new ThreadFactoryBuilder().setNameFormat("jersey-rx-client-completion-%d").build());
    }

    @GET
    @Path("/{user}")
    public void extendedProfile(@Suspended final AsyncResponse async,@PathParam("user") final String user) {



        final RxWebTarget<RxCompletionStageInvoker> rxScore = RxCompletionStage.from(score, executor);
        final Queue<String> errors = new ConcurrentLinkedQueue<>();

        final CompletionStage<CreditScore> scoreStage = rxScore.resolveTemplate("user", user)
                .request().rx().get(CreditScore.class)
                .exceptionally(throwable -> {
                    errors.offer("Forecast: " + throwable.getMessage());
                    return new CreditScore(-1);
                });

        final CompletionStage<Profile> profileStage = rxScore.resolveTemplate("user", user)
                .request().rx().get(Profile.class)
                .exceptionally(throwable -> {
                    errors.offer("Forecast: " + throwable.getMessage());
                    return new Profile("",-1);
                });

//        CompletableFuture.completedFuture(new Destination())
//                .thenCombine(scoreStage,)
//                .thenCombine(profileStage)


    }


}



