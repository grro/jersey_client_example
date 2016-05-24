package com.ccreanga.jersey.example.agent;

import com.ccreanga.jersey.example.dao.ProfileDao;
import com.ccreanga.jersey.example.domain.CreditScore;
import com.ccreanga.jersey.example.domain.Profile;
import com.ccreanga.jersey.example.domain.RentHistory;
import jersey.repackaged.com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.glassfish.jersey.client.rx.java8.RxCompletionStage;
import org.glassfish.jersey.server.Uri;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import java.util.Map;
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

    @Autowired
    ProfileDao profileDao;


    private final ExecutorService executor;


    public AsyncResource() {
        executor = new ScheduledThreadPoolExecutor(20,
                new ThreadFactoryBuilder().setNameFormat("jersey-rx-client-completion-%d").build());
    }

    @GET
    @Path("/{user}")
    public void userMaximumLoan(@Suspended final AsyncResponse async, @PathParam("user") final String user) throws ExecutionException, InterruptedException {

        UserExtendedProfile extendedProfile = new UserExtendedProfile();
        final Map<String, Throwable> errors = new ConcurrentHashMap<>();




        profileDao.findOneAsync(user)
                .thenApply(optionalProfile -> optionalProfile.<NotFoundException>orElseThrow(NotFoundException::new))
                .thenCompose(profile -> {
                    extendedProfile.setProfile(profile);
                    return CompletableFuture.completedFuture(extendedProfile)
                            .thenCombine(getCreditScoreFuture(errors, profile), UserExtendedProfile::setCreditScore)
                            .thenCombine(getRentHistoryFuture(errors, profile), UserExtendedProfile::setRentHistory)
                            .thenCompose(extProfile -> {
                                if (extProfile.allowCredit()) {//if it's eligible for a loan get the maximum loan value; if not return =1
                                    return getLoanFuture(errors, extProfile);
                                } else
                                    return CompletableFuture.completedFuture(-1);
                            });

                })
                .whenCompleteAsync((response, throwable) -> {
                    // todo - Do something with errors.
                    System.out.println(errors.toString());
                    async.resume(throwable == null ? response : throwable);

                });
    }

    private CompletionStage<Integer> getLoanFuture(Map<String, Throwable> errors, UserExtendedProfile u) {
        return RxCompletionStage.from(loan, executor)
                .resolveTemplate("creditScore", u.getCreditScore().get().getValue())
                .resolveTemplate("monthlyPayment", u.getProfile().get().getPayment())
                .request().rx().get(Integer.class)
                .exceptionally(throwable -> {
                    errors.put("Loan", throwable);
                    return -1;
                });
    }

    private CompletionStage<RentHistory> getRentHistoryFuture(Map<String, Throwable> errors, Profile profile) {
        return RxCompletionStage.from(rent, executor).resolveTemplate("uuid", profile.getRentUuid())
                                .request().rx().get(RentHistory.class)
                                .exceptionally(throwable -> {
                                    errors.put("RentHistory", throwable);
                                    return null;
                                });
    }

    private CompletionStage<CreditScore> getCreditScoreFuture(Map<String, Throwable> errors, Profile profile) {
        return RxCompletionStage.from(score, executor).resolveTemplate("uuid", profile.getCreditUuid())
                                .request().rx().get(CreditScore.class)
                                .exceptionally(throwable -> {
                                    errors.put("CreditScore", throwable);
                                    return null;
                                });
    }


}



