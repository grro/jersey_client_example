package com.ccreanga.jersey.example.agent;

import com.ccreanga.jersey.example.domain.CreditScore;
import com.ccreanga.jersey.example.domain.Profile;
import com.ccreanga.jersey.example.domain.RentHistory;

import java.util.Optional;

public class UserExtendedProfile {
    private Profile profile;
    private CreditScore creditScore;
    private RentHistory rentHistory;

    @Override
    public String toString() {
        return "UserExtendedProfile{" +
                "profile=" + profile +
                ", creditScore=" + creditScore +
                ", rentHistory=" + rentHistory +
                '}';
    }

    public UserExtendedProfile() {
    }

    public UserExtendedProfile setProfile(Profile profile) {
        this.profile = profile;
        return this;
    }

    public Optional<Profile> getProfile() {
        return profile==null?Optional.empty():Optional.of(profile);
    }

    public Optional<CreditScore> getCreditScore() {
        return creditScore==null?Optional.empty():Optional.of(creditScore);
    }

    public UserExtendedProfile setCreditScore(CreditScore creditScore) {
        this.creditScore = creditScore;
        return this;
    }

    public Optional<RentHistory> getRentHistory() {
        return rentHistory==null?Optional.empty():Optional.of(rentHistory);
    }

    public UserExtendedProfile setRentHistory(RentHistory rentHistory) {
        this.rentHistory = rentHistory;
        return this;
    }

    public boolean allowCredit() {
        return (creditScore!=null && creditScore.getValue() > 1) && (rentHistory!=null && rentHistory.getNoOfDelays() < 2);
    }
}
