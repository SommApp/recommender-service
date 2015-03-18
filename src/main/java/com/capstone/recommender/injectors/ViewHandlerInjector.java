package com.capstone.recommender.injectors;

import com.google.inject.AbstractModule;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author by sethwiesman on 2/15/15.
 */
public class ViewHandlerInjector extends AbstractModule {

    @Override
    protected void configure() {
        bind(Map.class).to(ConcurrentHashMap.class);
        bind(Collection.class).to(ArrayList.class);
    }
}
