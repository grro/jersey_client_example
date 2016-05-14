package com.ccreanga.jersey.example.remote;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.ccreanga.jersey.example.Helper;
import com.ccreanga.jersey.example.domain.Forecast;
import org.glassfish.jersey.server.ManagedAsync;


@Path("remote/forecast")
@Produces("application/xml")
public class ForecastResource {

    @GET
    @ManagedAsync
    @Path("/{destination}")
    public Forecast forecast(@PathParam("destination") final String destination) {
        // Simulate long-running operation.
        Helper.sleep(350);

        return new Forecast(destination, Helper.getForecast());
    }
}