package com.ccreanga.jersey.example.remote;

import java.util.Random;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.ccreanga.jersey.example.Helper;
import com.ccreanga.jersey.example.domain.Calculation;
import com.ccreanga.jersey.example.domain.CreditScore;
import org.glassfish.jersey.server.ManagedAsync;


@Path("remote/score")
@Produces("application/json")
public class CreditScoreResource {

    @GET
    @ManagedAsync
    @Path("/{user}")
    public CreditScore calculation(@PathParam("user") final String user) {
        // Simulate long-running operation.
        System.out.println("CreditScoreResource was invoked");
        Helper.sleep(350);

        return new CreditScore(new Random().nextInt(100));
    }
}
