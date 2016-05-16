package com.ccreanga.jersey.example.domain;


public class Destination {

    private String destination;

    public Destination() {
    }

    public Destination(final String destination) {
        this.destination = destination;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(final String destination) {
        this.destination = destination;
    }

    @Override
    public String toString() {
        return "Destination{" +
                "destination='" + destination + '\'' +
                '}';
    }
}
