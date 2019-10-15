package com.xuecheng.manage_cms;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Component;

@Configuration
@EnableMongoRepositories
@PropertySource("classpath:mongo.yml")
@Component
public class GridFsConfiguration extends AbstractMongoConfiguration {
    @Autowired
    Environment env;

    @Bean
    public GridFsTemplate gridFsTemplate() throws Exception {
        return new GridFsTemplate(mongoDbFactory(), mappingMongoConverter());
    }

    @Override
    protected String getDatabaseName() {
        return env.getProperty("database");
    }


    public Mongo mongo() throws Exception {
        return new MongoClient(env.getProperty("host"));
    }

    @Override
    public MongoClient mongoClient() {
        return null;
    }

    @Bean
    public MongoDbFactory mongoDbFactory()  {
        return new SimpleMongoDbFactory(new MongoClient(env.getProperty("host"), env.getProperty("port", Integer.class)), getDatabaseName());
    }


}
