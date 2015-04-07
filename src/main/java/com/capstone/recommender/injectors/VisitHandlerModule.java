package com.capstone.recommender.injectors;

import com.google.inject.AbstractModule;

import java.util.HashMap;
import java.util.Map;

/**
 *  @author sethwiesman on 3/27/15.
 */
public class VisitHandlerModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Map.class).to(HashMap.class);
    }
}
