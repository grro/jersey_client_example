package com.ccreanga.jersey.example.domain;

import java.util.ArrayList;
import java.util.List;


public class AgentResponse {

    private List<Destination> visited = new ArrayList<>();
    private List<Recommendation> recommended;
    private long processingTime;

    public AgentResponse() {
    }

    public List<Destination> getVisited() {
        return visited;
    }

    public void setVisited(final List<Destination> visited) {
        this.visited = visited;
    }

    public void setRecommended(final List<Recommendation> recommended) {
        this.recommended = recommended;
    }

    public List<Recommendation> getRecommended() {
        return recommended;
    }

    public void setProcessingTime(final long processingTime) {
        this.processingTime = processingTime;
    }

    public long getProcessingTime() {
        return processingTime;
    }

    public AgentResponse visited(final List<Destination> visited) {
        setVisited(visited);
        return this;
    }

    public AgentResponse recommended(final List<Recommendation> recommended) {
        setRecommended(recommended);
        return this;
    }
}
