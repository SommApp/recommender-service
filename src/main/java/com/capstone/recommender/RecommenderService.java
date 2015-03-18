package com.capstone.recommender;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class RecommenderService {
	public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        conf.addResource(new Path("/usr/local/hadoop-1.2.1/conf/core-site.xml"));
        conf.addResource(new Path("/usr/local/hadoop-1.2.1/conf/hdfs-site.xml"));
        FileSystem fs = FileSystem.get(conf);
        FSDataOutputStream out = fs.append(new Path("/demo.txt"));
        out.writeUTF("Append demo...");
        fs.close();

    }
}
