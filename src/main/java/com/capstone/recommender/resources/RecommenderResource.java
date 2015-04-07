package com.capstone.recommender.resources;

import com.capstone.recommender.controllers.Impls.EngineGeneratorFactory;
import com.capstone.recommender.controllers.Impls.StatisticsGeneratorFactory;
import com.capstone.recommender.controllers.RecommendationEngine;
import com.capstone.recommender.controllers.VisitHandler;

import com.capstone.recommender.injectors.RecommendationEngineModule;
import com.capstone.recommender.injectors.VisitHandlerModule;

import com.capstone.recommender.models.Analytic;
import com.capstone.recommender.models.CompleteVisit;
import com.capstone.recommender.models.Saying;

import com.google.common.base.Optional;
import com.google.inject.Guice;
import com.google.inject.Injector;

import com.yammer.metrics.annotation.Timed;

import org.apache.mahout.cf.taste.recommender.RecommendedItem;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class RecommenderResource {

    private static final Injector visitHandlerInjector;
    //private static final Injector recommendationEngineInjector;

    static {
        visitHandlerInjector = Guice.createInjector(new VisitHandlerModule());
        //recommendationEngineInjector = Guice.createInjector(new RecommendationEngineModule());
    }

	private final String template;
	private final String defaultName;
	private final AtomicLong counter;

    private final VisitHandler visitHandler;
    private final RecommendationEngine recommendationEngine;

	public RecommenderResource(String template, String defaultName) {
		this.template = template;
		this.defaultName = defaultName;
		this.counter = new AtomicLong();

        this.visitHandler = visitHandlerInjector.getInstance(VisitHandler.class);
        this.recommendationEngine = new RecommendationEngine(new EngineGeneratorFactory(), new StatisticsGeneratorFactory());
         //recommendationEngineInjector.getInstance(RecommendationEngine.class);
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
        java.util.Optional<CompleteVisit> optionalVisit =  visitHandler.endVisit(token);
        if (!optionalVisit.isPresent()) {
            return false;
        }

        recommendationEngine.addVisit(optionalVisit.get());
        return true;
    }

    @GET
    @Timed
    @Path("restaurant/recommend/{userId}")
    public List<RecommendedItem> getRecommendations(@PathParam("userId") int userId) {
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
