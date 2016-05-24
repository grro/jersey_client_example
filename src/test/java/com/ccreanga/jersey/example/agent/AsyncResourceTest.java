package com.ccreanga.jersey.example.agent;

import com.ccreanga.jersey.example.JerseyClientExampleApplication;
import com.ccreanga.jersey.example.remote.Constants;
import org.cassandraunit.CassandraCQLUnit;
import org.cassandraunit.dataset.cql.ClassPathCQLDataSet;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

import java.util.Optional;

import static org.junit.Assert.*;

@ActiveProfiles("dev")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = JerseyClientExampleApplication.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
public class AsyncResourceTest {

    @ClassRule
    public final static CassandraCQLUnit CASSANDRA =
            new CassandraCQLUnit(
                    new ClassPathCQLDataSet("cassandra-junit.cql", "test"),
                    EmbeddedCassandraServerHelper.DEFAULT_CASSANDRA_YML_FILE
            );

    private final Client client = ClientBuilder.newClient();

    @Value("${local.server.port}")
    private int port;



    @Test
    public void userMaximumLoan() throws Exception {
        try {
            client.target("http://localhost:" + port + "/rx/async/"+ Constants.uuid1).request(MediaType.APPLICATION_JSON)
                    .get(Integer.class);
        } catch (ClientErrorException clientError) {
            Assert.fail("got unexpected error " + clientError);
        }

    }
}