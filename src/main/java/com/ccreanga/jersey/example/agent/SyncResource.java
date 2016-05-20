package com.ccreanga.jersey.example.agent;

import com.ccreanga.jersey.example.domain.AgentResponse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("sync")
@Produces("application/json")
public class SyncResource {

    @GET
    public AgentResponse sync() {
        return null;
    }

}
