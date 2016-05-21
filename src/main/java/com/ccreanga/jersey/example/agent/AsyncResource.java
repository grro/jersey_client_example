package com.ccreanga.jersey.example.agent;

import com.ccreanga.jersey.example.domain.*;
import jersey.repackaged.com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.glassfish.jersey.client.rx.java8.RxCompletionStage;
import org.glassfish.jersey.server.Uri;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import java.util.Queue;
import java.util.concurrent.*;

@Path("async")
@Produces("application/json")
public class AsyncResource {

    @Uri("remote/score/{uuid}")
    private WebTarget score;

    @Uri("remote/profile/{uuid}")
    private WebTarget profile;

    @Uri("remote/rent/{uuid}")
    private WebTarget rent;

    @Uri("remote/loan/{creditScore}/{monthlyPayment}")
    private WebTarget loan;


    private final ExecutorService executor;


    private class UserData {
        Profile profile;
        CreditScore creditScore;
        RentHistory rentHistory;

        @Override
        public String toString() {
            return "UserData{" +
                    "profile=" + profile +
                    ", creditScore=" + creditScore +
                    ", rentHistory=" + rentHistory +
                    '}';
        }

        public UserData() {
        }

        public UserData setProfile(Profile profile) {
            this.profile = profile;
            return this;
        }

        public Profile getProfile() {
            return profile;
        }

        public CreditScore getCreditScore() {
            return creditScore;
        }

        public UserData setCreditScore(CreditScore creditScore) {
            this.creditScore = creditScore;
            return this;
        }

        public RentHistory getRentHistory() {
            return rentHistory;
        }

        public UserData setRentHistory(RentHistory rentHistory) {
            this.rentHistory = rentHistory;
            return this;
        }

        public boolean allowCredit() {
            return (creditScore.getValue() > 10) && (rentHistory.getNoOfDelays() > 1);
        }
    }

    public AsyncResource() {
        executor = new ScheduledThreadPoolExecutor(20,
                new ThreadFactoryBuilder().setNameFormat("jersey-rx-client-completion-%d").build());
    }

    @GET
    @Path("/{user}")
    public void userMaximumLoan(@Suspended final AsyncResponse async, @PathParam("user") final String user) throws ExecutionException, InterruptedException {


        UserData history = new UserData();
        final Queue<String> errors = new ConcurrentLinkedQueue<>();


        final CompletionStage<Profile> profileStage = RxCompletionStage.from(profile, executor).resolveTemplate("uuid", user)
                .request().rx().get(Profile.class)
                .exceptionally(throwable -> {
                    errors.offer("Profile: " + throwable.getMessage());
                    return null;
                });


        CompletableFuture.completedFuture(history)
                //get profiles and set them to userdata
                .thenCombine(profileStage, UserData::setProfile)
                .thenCompose(p -> {
                    Profile profile = p.getProfile();
                    CompletionStage<CreditScore> scoreStage = RxCompletionStage.from(score, executor).resolveTemplate("uuid", profile.getCreditUuid())
                            .request().rx().get(CreditScore.class)
                            .exceptionally(throwable -> {
                                errors.offer("CreditScore: " + throwable.getMessage());
                                return new CreditScore(-1);
                            });
                    CompletionStage<RentHistory> rentStage = RxCompletionStage.from(rent, executor).resolveTemplate("uuid", profile.getRentUuid())
                            .request().rx().get(RentHistory.class)
                            .exceptionally(throwable -> {
                                errors.offer("RentHistory: " + throwable.getMessage());
                                return new RentHistory(-1);
                            });

                    return CompletableFuture.completedFuture(p)
                            //get in parallel credit score and rent history and set them to user data
                            .thenCombine(scoreStage, UserData::setCreditScore)
                            .thenCombine(rentStage, UserData::setRentHistory)
                            //after the previous data is loaded check the credit score
                            .thenCompose(u -> {
                                System.out.println(u);
                                if (u.allowCredit()) {//if it's eligible for a loan get the maximum loan value; if not return =1

                                    return RxCompletionStage.from(loan, executor)
                                            .resolveTemplate("creditScore", u.getCreditScore().getValue())
                                            .resolveTemplate("monthlyPayment", u.getProfile().getMonthlyPayment())
                                            .request().rx().get(Integer.class)
                                            .exceptionally(throwable -> {
                                                errors.offer("Profile: " + throwable.getMessage());
                                                return -1;
                                            });
                                } else
                                    return CompletableFuture.completedFuture(-1);


                            });
                })
                .whenCompleteAsync((response, throwable) -> {
                    // Do something with errors.
                    async.resume(throwable == null ? response : throwable);
//                    System.out.println(response.getCreditScore());
//                    System.out.println(response.getRentHistory());


                });


//        UserData history = new UserData();
//
//        CompletableFuture<Profile> future = CompletableFuture.completedFuture(history)
//                .thenCombine(scoreStage, UserData::setCreditScore)
//                .thenCombine(rentStage, UserData::setRentHistory)
//                .thenCompose(p->{
//                    System.out.println(p);
//                    if (p.allowCredit()){
//
//                        return RxCompletionStage.from(profile, executor).resolveTemplate("user", user)
//                                .request().rx().get(Profile.class)
//                                .exceptionally(throwable -> {
//                                    errors.offer("Profile: " + throwable.getMessage());
//                                    return new Profile("",-1);
//                                });
//                    }
//                    else
//                        return CompletableFuture.completedFuture(new Profile());
//                })
//                .whenCompleteAsync((response, throwable) -> {
//                    // Do something with errors.
//                    async.resume(throwable == null ? response : throwable);
////                    System.out.println(response.getCreditScore());
////                    System.out.println(response.getRentHistory());
//
//
//                });


    }

}



