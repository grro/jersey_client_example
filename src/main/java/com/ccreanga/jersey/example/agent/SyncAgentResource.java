package com.ccreanga.jersey.example.agent;

import com.ccreanga.jersey.example.domain.*;
import org.glassfish.jersey.server.Uri;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@Path("agent/sync")
@Produces("application/json")
public class SyncAgentResource {

    @Uri("remote/destination")
    private WebTarget destination;

    @Uri("remote/calculation/from/{from}/to/{to}")
    private WebTarget calculation;

    @Uri("remote/forecast/{destination}")
    private WebTarget forecast;

//    @GET
//    public AgentResponse sync() {
//        final long time = System.nanoTime();
//
//        final AgentResponse response = new AgentResponse();
//        final Queue<String> errors = new ConcurrentLinkedQueue<>();
//
//        // Obtain visited destinations.
//        try {
//            response.setVisited(destination.path("visited").request()
//                    // Return a list of destinations
//                    .get(new GenericType<List<Destination>>() {
//                    }));
//        } catch (final Throwable throwable) {
//            errors.offer("Visited: " + throwable.getMessage());
//        }
//
//        // Obtain recommended destinations. (does not depend on visited ones)
//        List<Destination> recommended = Collections.emptyList();
//        try {
//            recommended = destination.path("recommended").request()
//                    // Return a list of destinations.
//                    .get(new GenericType<List<Destination>>() {
//                    });
//        } catch (final Throwable throwable) {
//            errors.offer("Recommended: " + throwable.getMessage());
//        }
//
//        // Forecasts. (depend on recommended destinations)
//        final Map<String, Forecast> forecasts = new HashMap<>();
//        for (final Destination dest : recommended) {
//            try {
//                forecasts.put(dest.getDestination(),
//                        forecast.resolveTemplate("destination", dest.getDestination()).request().get(Forecast.class));
//            } catch (final Throwable throwable) {
//                errors.offer("Forecast: " + throwable.getMessage());
//            }
//        }
//
//        // Calculations. (depend on recommended destinations)
//        final Map<String, Calculation> calculations = new HashMap<>();
//        recommended.stream().forEach(destination -> {
//            try {
//                calculations.put(destination.getDestination(), calculation.resolveTemplate("from", "Moon")
//                        .resolveTemplate("to", destination.getDestination())
//                        .request().get(Calculation.class));
//            } catch (final Throwable throwable) {
//                errors.offer("Calculation: " + throwable.getMessage());
//            }
//        });
//
//        // Recommendations.
//        final List<Recommendation> recommendations = new ArrayList<>(recommended.size());
//        for (final Destination dest : recommended) {
//            final Forecast fore = forecasts.get(dest.getDestination());
//            final Calculation calc = calculations.get(dest.getDestination());
//
//            recommendations.add(new Recommendation(dest.getDestination(),
//                    fore != null ? fore.getForecast() : "N/A", calc != null ? calc.getPrice() : -1));
//        }
//
//        // Do something with errors.
//        // ...
//
//        response.setRecommended(recommendations);
//        response.setProcessingTime((System.nanoTime() - time) / 1000000);
//        return response;
//    }
}
