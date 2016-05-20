package com.ccreanga.jersey.example.domain;

public class CreditScore {

    private int value;

    private CreditScore() {
    }

    public CreditScore(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    private void setValue(int value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CreditScore that = (CreditScore) o;

        return value == that.value;

    }

    @Override
    public int hashCode() {
        return value;
    }

    @Override
    public String toString() {
        return "CreditScore{" +
                "value=" + value +
                '}';
    }
}
