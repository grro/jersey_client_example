package com.ccreanga.jersey.example.remote;
import java.util.Random;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.ccreanga.jersey.example.Helper;
import com.ccreanga.jersey.example.domain.Profile;
import org.glassfish.jersey.server.ManagedAsync;


@Path("remote/profile")
@Produces("application/json")
public class ProfileResource {

    @GET
    @ManagedAsync
    @Path("/{user}")
    public Profile calculation(@PathParam("user") final String user) {
        System.out.println("ProfileResource was invoked");
        // Simulate long-running operation.
        Helper.sleep(350);

        return new Profile(user+"-name",18+new Random().nextInt(70));
    }
}
