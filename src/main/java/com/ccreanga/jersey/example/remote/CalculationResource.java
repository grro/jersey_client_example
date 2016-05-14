package com.ccreanga.jersey.example.remote;

import java.util.Random;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.ccreanga.jersey.example.Helper;
import com.ccreanga.jersey.example.domain.Calculation;
import org.glassfish.jersey.server.ManagedAsync;


@Path("remote/calculation")
@Produces("application/xml")
public class CalculationResource {

    @GET
    @ManagedAsync
    @Path("/from/{from}/to/{to}")
    public Calculation calculation(@PathParam("from") @DefaultValue("Moon") final String from,
                                   @PathParam("to") final String to) {
        // Simulate long-running operation.
        Helper.sleep(350);

        return new Calculation(from, to, new Random().nextInt(10000));
    }
}
