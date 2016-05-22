package com.ccreanga.jersey.example.remote;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import static com.ccreanga.jersey.example.remote.Constants.*;

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

    static Map<UUID,Profile> users = new ConcurrentHashMap<>();
    static{
        users.put(uuid1, new Profile("ion",uuid1_rent,uuid1_cr,uuid1,22,100));
        users.put(uuid2, new Profile("vasile",uuid2_rent,uuid2_cr,uuid2,28,140));
    }

    @GET
    @ManagedAsync
    @Path("/{uuid}")
    public Profile calculation(@PathParam("uuid") final UUID uuid) {
        System.out.println("ProfileResource was invoked at "+System.currentTimeMillis());
        // Simulate long-running operation.
        Helper.sleep(300);

        return users.get(uuid);
    }
}
