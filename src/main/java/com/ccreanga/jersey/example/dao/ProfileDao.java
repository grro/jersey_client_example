package com.ccreanga.jersey.example.dao;

import com.ccreanga.jersey.example.domain.Profile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cassandra.core.ConsistencyLevel;
import org.springframework.cassandra.core.QueryOptions;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Repository
public class ProfileDao extends AbstractDao {

    @Autowired
    public ProfileDao(final CassandraOperations cassandraOperations) {
        super(cassandraOperations);
    }

    public Optional<Profile> findOne(final String uuid) {
        return Optional.ofNullable(getOperations().selectOne("select * from profile where id = " + uuid + "", Profile.class));
    }


    public CompletableFuture<Optional<Profile>> findOneAsync(final String uuid) {
        final ObjectReadPromise<Profile> promise = new ObjectReadPromise<>();
        final QueryOptions qo = new QueryOptions();
        qo.setConsistencyLevel(ConsistencyLevel.ONE);
        getOperations().selectOneAsynchronously("select * from profile where id = " + uuid + "",
                Profile.class,
                promise,
                qo);
        return promise;
    }
}
