package com.ccreanga.jersey.example.remote;

import com.ccreanga.jersey.example.Helper;
import org.glassfish.jersey.server.ManagedAsync;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("remote/loan")
@Produces("application/json")
public class LoanResource {

    @GET
    @ManagedAsync
    @Path("/{creditScore}/{monthlyPayment}")
    public int calculation(@PathParam("creditScore") final int creditScore,@PathParam("monthlyPayment") final int monthlyPayment) {
        System.out.println("LoanResource was invoked at "+System.currentTimeMillis());
        // Simulate long-running operation.
        Helper.sleep(100);

        return (creditScore*monthlyPayment)/10;
    }

}
