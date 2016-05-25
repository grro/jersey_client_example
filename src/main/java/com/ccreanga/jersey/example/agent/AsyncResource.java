package com.ccreanga.jersey.example.agent;

import com.ccreanga.jersey.example.dao.ProfileDao;
import com.ccreanga.jersey.example.domain.CreditScore;
import com.ccreanga.jersey.example.domain.RentHistory;

import org.glassfish.jersey.client.rx.java8.RxCompletionStage;
import org.glassfish.jersey.server.Uri;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;

import java.util.UUID;
import java.util.concurrent.*;

import static com.ccreanga.jersey.example.agent.RxUtils.join;


@Path("async")
@Produces("application/json")
public class AsyncResource {

    // hateoas oriented clients will not compose a uri path. This is also true for templates. The structure of the 
    // uri path is not part of the client-server contract. 
    
    // Is RxWebTarget provided by the injection magic? e.g.
    // @Uri("remote/scores?q.uuid.eq={uuid}")
    // private RxWebTarget score;
    // However this seems also not to be a good style. It is also not hateos-ful -> URI path will be composed  
    
    
    
    @Uri("remote/scores/{uuid}")
    private WebTarget score;
    
    @Uri("remote/profiles/{uuid}")
    private WebTarget profile;

    @Uri("remote/rents/{uuid}")
    private WebTarget rent;

    @Uri("remote/loans/{creditScore}/{monthlyPayment}")
    private WebTarget loan;

    @Autowired
    ProfileDao profileDao;


    @GET
    @Path("/{user}")
    public void userMaximumLoanAsync(@Suspended final AsyncResponse async, @PathParam("user") final String user) {
        profileDao.findOneAsync(user)
                  .thenApply(optionalProfile -> optionalProfile.<NotFoundException>orElseThrow(NotFoundException::new))
                  .thenCompose(profile -> join(CompletableFuture.completedFuture(profile),
                                               getCreditScoreAsync(profile.getCreditUuid()),
                                               getRentHistoryAsync(profile.getRentUuid())))
                  .thenApply(triResult -> UserExtendedProfile.create(triResult.getResult1())
                                                             .withCreditScore(triResult.getResult2())
                                                             .withRentHistory(triResult.getResult3()))
                  .thenCompose(extProfile -> CompletableFuture.completedFuture(extProfile))
                  .thenCompose(extProfile -> extProfile.allowCredit() ? getLoanAsync(extProfile.getCreditScore().getValue(),  //if it's eligible for a loan get the maximum loan value; if not return =1
                                                                                     extProfile.getProfile().getPayment())  
                                                                      : CompletableFuture.completedFuture(-1))
                  .exceptionally(error -> { throw new InternalServerErrorException(error); })                                // todo add error handling
                  .whenComplete((extProfile, throwable) -> async.resume(throwable == null ? extProfile : throwable));        // exception unwraping is missing
    }

    private CompletionStage<Integer> getLoanAsync(final int creditScore, final int payment) {
        return RxCompletionStage.from(loan)
                                .resolveTemplate("creditScore", creditScore)
                                .resolveTemplate("monthlyPayment", payment)
                                .request()
                                .rx()
                                .get(Integer.class);
    }

    private CompletionStage<RentHistory> getRentHistoryAsync(final UUID rentUuid) {
        return RxCompletionStage.from(rent).resolveTemplate("uuid", rentUuid)
                                .request()
                                .rx()
                                .get(RentHistory.class);
    }

    private CompletionStage<CreditScore> getCreditScoreAsync(final UUID creditUuid) {
        return RxCompletionStage.from(score).resolveTemplate("uuid", creditUuid)
                                .request()
                                .rx()
                                .get(CreditScore.class);
    }   
}



