package com.ccreanga.jersey.example.agent;

import com.ccreanga.jersey.example.domain.CreditScore;

import com.ccreanga.jersey.example.domain.Profile;
import com.ccreanga.jersey.example.domain.RentHistory;



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

    private UserExtendedProfile(Profile profile, CreditScore creditScore, RentHistory rentHistory) {
        this.profile = profile;
        this.creditScore = creditScore;
        this.rentHistory = rentHistory;
    }

    public UserExtendedProfile setProfile(Profile profile) {
        this.profile = profile;
        return this;
    }

    public Profile getProfile() {
        return profile;
    }

    public CreditScore getCreditScore() {
        return creditScore;
    }

    public UserExtendedProfile setCreditScore(CreditScore creditScore) {
        this.creditScore = creditScore;
        return this;
    }

    public RentHistory getRentHistory() {
        return rentHistory;
    }

    public UserExtendedProfile setRentHistory(RentHistory rentHistory) {
        this.rentHistory = rentHistory;
        return this;
    }

    ///////////////////////////
    // convenience methods
    
    public static UserExtendedProfile create(Profile profile) {
        return new UserExtendedProfile(profile, null, null);
    }
    
    public UserExtendedProfile withCreditScore(CreditScore creditScore) {
        return new UserExtendedProfile(this.profile, creditScore, this.rentHistory);
    }

    public UserExtendedProfile withRentHistory(RentHistory rentHistory) {
        return new UserExtendedProfile(this.profile, this.creditScore, rentHistory);
    }
    
    public boolean allowCredit() {
        return (creditScore!=null && creditScore.getValue() > 1) && (rentHistory!=null && rentHistory.getNoOfDelays() < 2);
    }
}
