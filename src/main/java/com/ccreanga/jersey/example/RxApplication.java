package com.ccreanga.jersey.example;

import com.ccreanga.jersey.example.agent.AsyncAgentResource;
import com.ccreanga.jersey.example.agent.SyncAgentResource;
import com.ccreanga.jersey.example.remote.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.ext.ContextResolver;

@Configuration
@ApplicationPath("rx")
public class RxApplication extends ResourceConfig {

    public RxApplication() {
        // Remote (Server) Resources.

        register(CreditScoreResource.class);
        register(ProfileResource.class);
        register(RentHistoryResource.class);


        register(DestinationResource.class);
        register(CalculationResource.class);
        register(ForecastResource.class);

        // Agent (Client) Resources.
        register(SyncAgentResource.class);
        register(AsyncAgentResource.class);

        // Providers.
        register(JacksonFeature.class);
        register(ObjectMapperProvider.class);
    }

    public static class ObjectMapperProvider implements ContextResolver<ObjectMapper> {

        @Override
        public ObjectMapper getContext(final Class<?> type) {
            return new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        }
    }
}