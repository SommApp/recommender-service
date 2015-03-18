package com.capstone.recommender;

import com.capstone.recommender.health.TemplateHealthCheck;
import com.capstone.recommender.resources.RecommenderResource;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Environment;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class RecommenderService extends Service<RecommenderConfiguration> {
	public static void main(String[] args) throws Exception {
		//new RecommenderService().run(args);

        Configuration conf = new Configuration();
        conf.addResource(new Path("/usr/local/hadoop-1.2.1/conf/core-site.xml"));
        conf.addResource(new Path("/usr/local/hadoop-1.2.1/conf/hdfs-site.xml"));
        FileSystem fs = FileSystem.get(conf);
        FSDataOutputStream out = fs.append(new Path("/demo.txt"));
        out.writeUTF("Append demo...");
        fs.close();

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
