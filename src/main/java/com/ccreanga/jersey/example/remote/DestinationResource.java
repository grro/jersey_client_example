package com.ccreanga.jersey.example.remote;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import javax.inject.Singleton;

import com.ccreanga.jersey.example.Helper;
import com.ccreanga.jersey.example.domain.Destination;
import org.glassfish.jersey.server.ManagedAsync;

import com.google.common.collect.Lists;


@Singleton
@Path("remote/destination")
@Produces("application/json")
public class DestinationResource {

    private static final Map<String, List<String>> VISITED = new HashMap<>();

    static {
        VISITED.put("Sync", Helper.getCountries(5));
        VISITED.put("Async", Helper.getCountries(5));
        VISITED.put("Guava", Helper.getCountries(5));
        VISITED.put("RxJava", Helper.getCountries(5));
        VISITED.put("Java8", Helper.getCountries(5));
    }

    @GET
    @ManagedAsync
    @Path("visited")
    public List<Destination> visited(@HeaderParam("Rx-User") @DefaultValue("KO") final String user) {
        // Simulate long-running operation.
        Helper.sleep();

        if (!VISITED.containsKey(user)) {
            VISITED.put(user, Helper.getCountries(5));
        }

        return Lists.transform(VISITED.get(user), Destination::new);
    }

    @GET
    @ManagedAsync
    @Path("recommended")
    public List<Destination> recommended(@HeaderParam("Rx-User") @DefaultValue("KO") final String user,
                                         @QueryParam("limit") @DefaultValue("5") final int limit) {
        // Simulate long-running operation.
        Helper.sleep();

        if (!VISITED.containsKey(user)) {
            VISITED.put(user, Helper.getCountries(5));
        }

        return Lists.transform(Helper.getCountries(limit, VISITED.get(user)), Destination::new);
    }
}