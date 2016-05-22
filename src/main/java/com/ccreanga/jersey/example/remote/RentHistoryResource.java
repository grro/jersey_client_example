package com.ccreanga.jersey.example.remote;

import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.ccreanga.jersey.example.Helper;
import com.ccreanga.jersey.example.domain.Calculation;
import com.ccreanga.jersey.example.domain.CreditScore;
import com.ccreanga.jersey.example.domain.RentHistory;
import org.glassfish.jersey.server.ManagedAsync;

import static com.ccreanga.jersey.example.remote.Constants.uuid1_cr;
import static com.ccreanga.jersey.example.remote.Constants.uuid1_rent;
import static com.ccreanga.jersey.example.remote.Constants.uuid2_rent;


@Path("remote/rent")
@Produces("application/json")
public class RentHistoryResource {

    static Map<UUID,RentHistory> history = new ConcurrentHashMap<>();
    static{
        history.put(uuid1_rent, new RentHistory(1));
        history.put(uuid2_rent, new RentHistory(2));
    }

    @GET
    @ManagedAsync
    @Path("/{uuid}")
    public Response calculation(@PathParam("uuid") final UUID uuid) {
        System.out.println("RentHistoryResource was invoked at "+System.currentTimeMillis());
        // Simulate long-running operation.
        Helper.sleep(350);
        RentHistory rentHistory = history.get(uuid);
        if (rentHistory!=null)
            return Response.ok(rentHistory).build();
        return Response.status(Response.Status.NOT_FOUND).build();

    }
}
