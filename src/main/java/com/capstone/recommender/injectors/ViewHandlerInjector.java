package com.capstone.recommender.injectors;

import com.google.common.cache.CacheBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.Configuration;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

/**
 * @author by sethwiesman on 2/15/15.
 */
public class ViewHandlerInjector extends AbstractModule {

    @Override
    protected void configure() {
        //bind(CacheBuilder.class).to(CacheBuilder.class);
        bind(OutputStream.class).to(OutputStream.class);
    }

    @Provides
    CacheBuilder provideCacheBuilder() {
        return CacheBuilder.newBuilder();
    }

    @Provides
    OutputStream provideOutputStream() {
        final String dest = "/usr/restaurant/visits";
        final Configuration conf = new Configuration();
        try {
            final FileSystem fs = FileSystem.get(URI.create(dest), conf);
            final OutputStream outputStream = fs.create(new Path(dest), () -> {
                System.out.print("*");
            });

            return outputStream;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
