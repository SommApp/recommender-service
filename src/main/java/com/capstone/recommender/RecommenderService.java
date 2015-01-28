package com.capstone.recommender;

import com.capstone.recommender.health.TemplateHealthCheck;
import com.capstone.recommender.resources.RecommenderResource;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Environment;

public class RecommenderService extends Service<RecommenderConfiguration> {
	public static void main(String[] args) throws Exception {
		new RecommenderService().run(args);
	}

	private RecommenderService() {
		super("recommender-service");
	}

	@Override
	protected void initialize(RecommenderConfiguration configuration, Environment environment) {
		final String template = configuration.getTemplate();
		final String defaultName = configuration.getDefaultName();
		environment.addResource(new RecommenderResource(template, defaultName));
		environment.addHealthCheck(new TemplateHealthCheck(template));
    }
}
