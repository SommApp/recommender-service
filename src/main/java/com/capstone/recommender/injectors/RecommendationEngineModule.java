package com.capstone.recommender.injectors;

import com.capstone.recommender.controllers.Impls.EngineGeneratorFactory;
import com.google.inject.AbstractModule;

/**
 * @author sethwiesman on 3/27/15.
 */
public class RecommendationEngineModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(EngineGeneratorFactory.class).to(EngineGeneratorFactory.class);
    }
}
