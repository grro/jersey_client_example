package com.ccreanga.jersey.example.domain;

public class Decision {

    private String name;
    private boolean approved;

    public Decision() {
    }

    public Decision(String name, boolean approved) {
        this.name = name;
        this.approved = approved;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Decision decision = (Decision) o;

        if (approved != decision.approved) return false;
        return name != null ? name.equals(decision.name) : decision.name == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (approved ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Decision{" +
                "name='" + name + '\'' +
                ", approved=" + approved +
                '}';
    }
}
