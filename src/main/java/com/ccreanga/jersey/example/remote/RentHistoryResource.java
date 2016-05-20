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
import com.ccreanga.jersey.example.domain.RentHistory;
import org.glassfish.jersey.server.ManagedAsync;


@Path("remote/rent")
@Produces("application/json")
public class RentHistoryResource {

    @GET
    @ManagedAsync
    @Path("/{user}")
    public RentHistory calculation(@PathParam("user") final String user) {
        System.out.println("RentHistoryResource was invoked");
        // Simulate long-running operation.
        Helper.sleep(350);

        return new RentHistory(new Random().nextInt(5));
    }
}
