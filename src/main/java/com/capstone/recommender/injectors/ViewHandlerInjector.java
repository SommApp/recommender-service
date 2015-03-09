package com.capstone.recommender.injectors;

import com.capstone.recommender.RecommenderConstants;
import com.google.common.cache.CacheBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.Configuration;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author by sethwiesman on 2/15/15.
 */
public class ViewHandlerInjector extends AbstractModule {

    @Override
    protected void configure() {
        bind(Map.class).to(ConcurrentHashMap.class);
        bind(OutputStream.class).to(OutputStream.class);
    }

    @Provides
    OutputStream provideOutputStream() {
        final String dest = RecommenderConstants.VISIT_FILE;
        final Configuration conf = new Configuration();
        try {
            final FileSystem fs = FileSystem.get(URI.create(dest), conf);

            return fs.create(new Path(dest), () -> {
                System.out.print("*");
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
