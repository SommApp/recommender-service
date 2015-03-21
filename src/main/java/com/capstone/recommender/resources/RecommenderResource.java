package com.capstone.recommender.resources;

import com.capstone.recommender.controllers.VisitHandler;
import com.capstone.recommender.models.Analytic;
import com.capstone.recommender.models.Saying;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.yammer.metrics.annotation.Timed;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.atomic.AtomicLong;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class RecommenderResource {
	private final String template;
	private final String defaultName;
	private final AtomicLong counter;

    private final VisitHandler visitHandler;

	public RecommenderResource(String template, String defaultName) {
		this.template = template;
		this.defaultName = defaultName;
		this.counter = new AtomicLong();

        this.visitHandler = new VisitHandler();
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
    @Path("visit/restaurant/{userId}/{restaurantId}")
    public long beginRestaurantVisit(@PathParam("userId") long userId, @PathParam("restaurantId") long restaurantId) {
        return visitHandler.beginVisit(userId, restaurantId);
    }

    @PUT
    @Timed
    @Path("visit/restaurant/{token}")
    public boolean endRestaurantVisit(@PathParam("token") long token) {
        return visitHandler.endVisit(token);
    }

    @GET
    @Timed
    @Path("restaurant/recommend/{userId}")
    public ImmutableList<Long> getRecommendations(@PathParam("userId") long userId) {
        return null;
    }

    @GET
    @Timed
    @Path("restaurant/analytics/{restaurantId}")
    public Analytic getAnalytics(@PathParam("restaurantId") long restaurantId) {
        return null;
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
