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
import com.ccreanga.jersey.example.domain.Profile;
import org.glassfish.jersey.server.ManagedAsync;

import static com.ccreanga.jersey.example.remote.Constants.*;


@Path("remote/score")
@Produces("application/json")
public class CreditScoreResource {


    static Map<UUID,CreditScore> score = new ConcurrentHashMap<>();
    static{
        score.put(uuid1_cr, new CreditScore(2));
        score.put(uuid2_cr, new CreditScore(5));
    }


    @GET
    @ManagedAsync
    @Path("/{uuid}")
    public Response calculation(@PathParam("uuid") final UUID uuid) {
        // Simulate long-running operation.
        System.out.println("CreditScoreResource was invoked at "+System.currentTimeMillis());
        Helper.sleep(350);
        CreditScore creditScore = score.get(uuid);
        if (creditScore!=null)
            return Response.ok(creditScore).build();
        return Response.status(Response.Status.NOT_FOUND).build();

    }

}
