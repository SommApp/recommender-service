package com.capstone.recommender.resources;

import com.capstone.recommender.controllers.RecommendationEngine;

import com.capstone.recommender.models.Analytic;
import com.capstone.recommender.models.Saying;

import com.capstone.recommender.models.Visit;
import com.google.common.base.Optional;

import com.yammer.metrics.annotation.Timed;

import org.apache.mahout.cf.taste.recommender.RecommendedItem;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class RecommenderResource {

	private final String template;
	private final String defaultName;
	private final AtomicLong counter;

    private final RecommendationEngine recommendationEngine;

	public RecommenderResource(String template, String defaultName) {
		this.template = template;
		this.defaultName = defaultName;
		this.counter = new AtomicLong();

        this.recommendationEngine = new RecommendationEngine();
    }

	@GET
	@Timed
    @Path("hello-world")
	public Saying sayHello(@QueryParam("name") Optional<String> name) {
		return new Saying(counter.incrementAndGet(),
			String.format(template, name.or(defaultName)));
	}


    @POST
    @Timed
    @Path("visit/restaurant/{uid}/{rid}/{len}")
    public boolean restaurantVisit(@PathParam("uid") long uid, @PathParam("rid") long rid, @PathParam("len") long len) {
        recommendationEngine.addVisit(new Visit(uid, rid, len));
        return true;
    }

    @POST
    @Timed
    @Path("visit/restaurant/full/{uid}/{rid}/{len}/{date}")
    public boolean fullRestaurantVisit(@PathParam("uid") long uid,
                                       @PathParam("rid") long rid,
                                       @PathParam("len") long len,
                                       @PathParam("date") long date) {
        recommendationEngine.addVisit(new Visit(uid, rid, len, date));
        return true;
    }

    @GET
    @Timed
    @Path("restaurant/recommend/{userId}")
    public Set<RecommendedItem> getRecommendations(@PathParam("userId") int userId) {
        return recommendationEngine.getRecommendations(userId);
    }

    @GET
    @Timed
    @Path("restaurant/analytics/{restaurantId}")
    public Analytic getAnalytics(@PathParam("restaurantId") long restaurantId) {
        return recommendationEngine.getAnalytics(restaurantId);
    }


    @POST
    @Timed
    @Path("restaurant/analytics/view/{userId}/{restaurantId}")
    public void viewRestaurant(@PathParam("userId") long userId, @PathParam("restaurantId") long restaurantId) {

    }

    @POST
    @Timed
    @Path("app/analytics/open/{userId}")
    public void openApp(@PathParam("userId") long userId) {

    }
}
