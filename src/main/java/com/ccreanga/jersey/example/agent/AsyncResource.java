package com.ccreanga.jersey.example.agent;

import com.ccreanga.jersey.example.domain.*;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.Session;
import jersey.repackaged.com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.oneandone.troilus.Dao;
import net.oneandone.troilus.DaoImpl;
import org.glassfish.jersey.client.rx.java8.RxCompletionStage;
import org.glassfish.jersey.server.Uri;

import javax.ws.rs.*;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import java.util.Map;
import java.util.UUID;
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



    public AsyncResource() {
        executor = new ScheduledThreadPoolExecutor(20,
                new ThreadFactoryBuilder().setNameFormat("jersey-rx-client-completion-%d").build());
    }

    @GET
    @Path("/{user}")
    public void userMaximumLoan(@Suspended final AsyncResponse async, @PathParam("user") final String user) throws ExecutionException, InterruptedException {

        UserData history = new UserData();
        final Map<String,Throwable> errors = new ConcurrentHashMap<>();

        Dao hotelsDao = new DaoImpl(null, "hotels");


                CompletableFuture<Integer> daoFuture = hotelsDao.readWithKey("id", 2)
                .asEntity(Profile.class)
                .withConsistency(ConsistencyLevel.QUORUM)
                .executeAsync()
                        .thenApply(optionalHotel -> optionalHotel.<NotFoundException>orElseThrow(NotFoundException::new))
                        .thenCompose(profile -> {

                                CompletionStage<CreditScore> scoreStage = RxCompletionStage.from(score, executor).resolveTemplate("uuid", profile.getCreditUuid())
                                        .request().rx().get(CreditScore.class)
                                        .exceptionally(throwable -> {
                                            errors.put("CreditScore",throwable);
                                            return null;
                                        });
                                CompletionStage<RentHistory> rentStage = RxCompletionStage.from(rent, executor).resolveTemplate("uuid", profile.getRentUuid())
                                        .request().rx().get(RentHistory.class)
                                        .exceptionally(throwable -> {
                                            errors.put("RentHistory",throwable);
                                            return null;
                                        });
                                history.setProfile(profile);
                                return CompletableFuture.completedFuture(history)
                                        //get in parallel credit score and rent history and set them to user data
                                        .thenCombine(scoreStage, UserData::setCreditScore)
                                        .thenCombine(rentStage, UserData::setRentHistory)
                                        //after the previous data is loaded check the credit score
                                        .thenCompose(u -> {
                                            System.out.println(u);
                                            if (u.allowCredit()) {//if it's eligible for a loan get the maximum loan value; if not return =1

                                                return RxCompletionStage.from(loan, executor)
                                                        .resolveTemplate("creditScore", u.getCreditScore().get().getValue())
                                                        .resolveTemplate("monthlyPayment", u.getProfile().get().getMonthlyPayment())
                                                        .request().rx().get(Integer.class)
                                                        .exceptionally(throwable -> {
                                                            errors.put("Loan",throwable);
                                                            return -1;
                                                        });
                                            } else
                                                return CompletableFuture.completedFuture(-1);


                                        });


                        })
                        .whenCompleteAsync((response, throwable) -> {
                            // todo - Do something with errors.
                            System.out.println(errors.toString());
                            async.resume(throwable == null ? response : throwable);

                        });





//        final CompletionStage<Profile> profileStage = RxCompletionStage.from(profile, executor).resolveTemplate("uuid", user)
//                .request().rx().get(Profile.class)
//                .exceptionally(throwable -> {
//                    errors.put("Profile",throwable);
//                    return null;
//                });
//
//        CompletableFuture.completedFuture(history)
//                //get profile and set them to userdata
//                .thenCombine(profileStage, UserData::setProfile)
//                .thenCompose(p -> {
//                    Optional<Profile> optionalProfile = p.getProfile();
//
//                    if (optionalProfile.isPresent()){
//                        Profile profile = optionalProfile.get();
//                        CompletionStage<CreditScore> scoreStage = RxCompletionStage.from(score, executor).resolveTemplate("uuid", profile.getCreditUuid())
//                                .request().rx().get(CreditScore.class)
//                                .exceptionally(throwable -> {
//                                    errors.put("CreditScore",throwable);
//                                    return null;
//                                });
//                        CompletionStage<RentHistory> rentStage = RxCompletionStage.from(rent, executor).resolveTemplate("uuid", profile.getRentUuid())
//                                .request().rx().get(RentHistory.class)
//                                .exceptionally(throwable -> {
//                                    errors.put("RentHistory",throwable);
//                                    return null;
//                                });
//
//                        return CompletableFuture.completedFuture(p)
//                                //get in parallel credit score and rent history and set them to user data
//                                .thenCombine(scoreStage, UserData::setCreditScore)
//                                .thenCombine(rentStage, UserData::setRentHistory)
//                                //after the previous data is loaded check the credit score
//                                .thenCompose(u -> {
//                                    System.out.println(u);
//                                    if (u.allowCredit()) {//if it's eligible for a loan get the maximum loan value; if not return =1
//
//                                        return RxCompletionStage.from(loan, executor)
//                                                .resolveTemplate("creditScore", u.getCreditScore().get().getValue())
//                                                .resolveTemplate("monthlyPayment", u.getProfile().get().getMonthlyPayment())
//                                                .request().rx().get(Integer.class)
//                                                .exceptionally(throwable -> {
//                                                    errors.put("Loan",throwable);
//                                                    return -1;
//                                                });
//                                    } else
//                                        return CompletableFuture.completedFuture(-1);
//
//
//                                });
//
//                    }else
//                    return CompletableFuture.completedFuture(-1);
//
//                })
//                .whenCompleteAsync((response, throwable) -> {
//                    // todo - Do something with errors.
//                    System.out.println(errors.toString());
//                    async.resume(throwable == null ? response : throwable);
//
//                });

//        hotelsDao.readWithKey("id", hotelId)
//                .asEntity(Hotel.class)
//                .executeAsync()
//                .thenApply(optionalHotel -> optionalHotel.<NotFoundException>orElseThrow(NotFoundException::new))
//                .thenCompose(hotel -> restClient.target(hotel.getPictureUri())
//                        .request()
//                        .rx()
//                        .get(byte[].class)
//                        .exceptionally(error -> defaultPicture))
//                .thenApply(picture -> resize(picture, height, width, "png"))
//                .whenComplete(ResultConsumer.writeTo(resp));
    }

    public static void main(String[] args) {
         WebTarget score = ClientBuilder.newClient().target("http://localhost:8080/rx/remote/score/{uuid}");

         WebTarget rent = ClientBuilder.newClient().target("http://localhost:8080/rx/remote/rent/{uuid}");

         WebTarget loan = ClientBuilder.newClient().target("http://localhost:8080/rx/remote/loan/{creditScore}/{monthlyPayment}");


        Cluster cluster = Cluster.builder()
                .addContactPoint("172.17.0.2")

                .withPort(9042)
                .build();


        Session session = cluster.connect("test");

        ExecutorService executor = new ScheduledThreadPoolExecutor(20,
                new ThreadFactoryBuilder().setNameFormat("jersey-rx-client-completion-%d").build());
        UserData history = new UserData();
        final Map<String,Throwable> errors = new ConcurrentHashMap<>();
        Dao hotelsDao = new DaoImpl(session, "profile");

        System.out.println(hotelsDao.readWithKey("id", UUID.fromString("ef3e4158-c0e0-41bf-b9e9-5f0179183abd")).asEntity(Profile.class).execute());

        CompletableFuture<Integer> daoFuture = hotelsDao.readWithKey("id", UUID.fromString("df3e4158-c0e0-41bf-b9e9-5f0179183abd"))
                .asEntity(Profile.class)
                .withConsistency(ConsistencyLevel.QUORUM)
                .executeAsync()
                .thenApply(optionalProfile -> optionalProfile.<NotFoundException>orElseThrow(NotFoundException::new))
                .thenCompose(profile -> {

                    CompletionStage<CreditScore> scoreStage = RxCompletionStage.from(score, executor).resolveTemplate("uuid", profile.getCreditUuid())
                            .request().rx().get(CreditScore.class)
                            .exceptionally(throwable -> {
                                errors.put("CreditScore",throwable);
                                return null;
                            });
                    CompletionStage<RentHistory> rentStage = RxCompletionStage.from(rent, executor).resolveTemplate("uuid", profile.getRentUuid())
                            .request().rx().get(RentHistory.class)
                            .exceptionally(throwable -> {
                                errors.put("RentHistory",throwable);
                                return null;
                            });
                    history.setProfile(profile);
                    return CompletableFuture.completedFuture(history)
                            //get in parallel credit score and rent history and set them to user data
                            .thenCombine(scoreStage, UserData::setCreditScore)
                            .thenCombine(rentStage, UserData::setRentHistory)
                            //after the previous data is loaded check the credit score
                            .thenCompose(u -> {
                                System.out.println(u);
                                if (u.allowCredit()) {//if it's eligible for a loan get the maximum loan value; if not return =1

                                    return RxCompletionStage.from(loan, executor)
                                            .resolveTemplate("creditScore", u.getCreditScore().get().getValue())
                                            .resolveTemplate("monthlyPayment", u.getProfile().get().getMonthlyPayment())
                                            .request().rx().get(Integer.class)
                                            .exceptionally(throwable -> {
                                                errors.put("Loan",throwable);
                                                return -1;
                                            });
                                } else
                                    return CompletableFuture.completedFuture(-1);


                            });


                })
                .whenCompleteAsync((response, throwable) -> {
                    // todo - Do something with errors.
                    System.out.println(errors.toString());
                    System.out.println(response);

                });

    }



}



