package com.ccreanga.jersey.example.dao;

import java.util.Collection;
import java.util.Optional;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cassandra.core.QueryForObjectListener;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.WriteListener;
import org.springframework.stereotype.Repository;

@Repository
public abstract class AbstractDao {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractDao.class);


    private final CassandraOperations cassandraOperations;

    @Autowired
    public AbstractDao( final CassandraOperations cassandraOperations) {
        this.cassandraOperations = cassandraOperations;
    }

    protected CassandraOperations getOperations() {
        return cassandraOperations;
    }


    public static class ObjectInsertPromise<T> extends CompletableFuture<T> implements WriteListener<T> {
        protected final T obj;

        public ObjectInsertPromise(final T obj) {
            this.obj = obj;
        }

        @Override
        public void onWriteComplete(final Collection<T> entities) {
            this.complete(obj);
        }

        @Override
        public void onException(final Exception ex) {
            this.completeExceptionally(ex);
        }
    }


    public static final class OptionalObjectInsertPromise<T> extends ObjectInsertPromise<T> {

        public OptionalObjectInsertPromise(final T obj) {
            super(obj);
        }

        @Override
        public void onException(final Exception ex) {
            LOG.warn("error occured by storing " + obj, ex);
            this.complete(obj);
        }
    }

    public static final class ObjectReadPromise<T> extends CompletableFuture<Optional<T>> implements QueryForObjectListener<T> {

        // BUG: method will not be invoked in case of an empty result -> https://jira.spring.io/browse/DATACASS-287

        @Override
        public void onQueryComplete(final T result) {
            this.complete(Optional.ofNullable(result));
        }

        @Override
        public void onException(final Exception ex) {
            this.completeExceptionally(ex);
        }
    }
}