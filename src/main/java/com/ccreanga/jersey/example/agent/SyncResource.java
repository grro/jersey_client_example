package com.ccreanga.jersey.example.agent;

import com.ccreanga.jersey.example.dao.ProfileDao;
import com.ccreanga.jersey.example.domain.CreditScore;
import com.ccreanga.jersey.example.domain.Profile;
import com.ccreanga.jersey.example.domain.RentHistory;
import org.glassfish.jersey.server.Uri;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor;

import javax.ws.rs.*;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

@Path("sync")
@Produces("application/json")
public class SyncResource {

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

    @GET
    @Path("/{user}")
    public Response userMaximumLoan(@PathParam("user") final String user) {

        Optional<Profile> optionalProfile = profileDao.findOne(user);
        if (!optionalProfile.isPresent())
            throw new NotFoundException();
        Profile profile = optionalProfile.get();

        CreditScore creditScore = score.resolveTemplate("uuid", profile.getCreditUuid()).request().get(CreditScore.class);
        RentHistory history = rent.resolveTemplate("uuid", profile.getRentUuid()).request().get(RentHistory.class);
        UserExtendedProfile extendedProfile = new UserExtendedProfile(profile,creditScore,history);
        Integer maximumLoan = -1;
        if (extendedProfile.allowCredit())
            maximumLoan =  loan.
                    resolveTemplate("creditScore",  creditScore.getValue()).
                    resolveTemplate("monthlyPayment",  history.getNoOfDelays()).
                    request().get(Integer.class);

        return Response.ok().entity(maximumLoan).type(MediaType.APPLICATION_JSON).build();
    }

}
