package com.ccreanga.jersey.example.domain;


public class RentHistory {

    private int noOfDelays;

    private RentHistory() {
    }

    public RentHistory(int noOfDelays) {
        this.noOfDelays = noOfDelays;
    }

    public int getNoOfDelays() {
        return noOfDelays;
    }

    private void setNoOfDelays(int noOfDelays) {
        this.noOfDelays = noOfDelays;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RentHistory that = (RentHistory) o;

        return noOfDelays == that.noOfDelays;

    }

    @Override
    public int hashCode() {
        return noOfDelays;
    }

    @Override
    public String toString() {
        return "RentHistory{" +
                "noOfDelays=" + noOfDelays +
                '}';
    }
}
