package com.ccreanga.jersey.example.remote;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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


@Singleton
@Path("remote/destination")
@Produces("application/json")
public class DestinationResource {

    @GET
    @ManagedAsync
    @Path("visited")
    public List<Destination> visited() {
        Helper.sleep();
        return Helper.getCountries(5).stream().map(Destination::new).collect(Collectors.toList());
    }

    @GET
    @ManagedAsync
    @Path("recommended")
    public List<Destination> recommended() {
        Helper.sleep();
        List<String> excluded = Helper.getCountries(5);
        return Helper.getCountries(5, excluded).stream().map(Destination::new).collect(Collectors.toList());
    }
}