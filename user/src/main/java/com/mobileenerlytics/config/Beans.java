package com.mobileenerlytics.config;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty;
import org.springframework.web.client.RestTemplate;

@Configuration
public class Beans {

    @Autowired
    private DbProperties dbProperties;

    @Bean
    public RestTemplate restTemplate() {return new RestTemplate();}

    @Bean
    public MongoDatabase mongoDatabase() {
        String mongoUrl = dbProperties.getUri();
        MongoClientURI connectionString = new MongoClientURI(mongoUrl);
        MongoClient mongoClient = new MongoClient(connectionString);
        String dbName = dbProperties.getDatabase();
        return mongoClient.getDatabase(dbName);
    }

//    @Bean
//    public CustomMongoConverter customMongoConverter(MongoDbFactory mongoDbFactory, MappingContext<? extends MongoPersistentEntity<?>, MongoPersistentProperty> mappingContext, CustomConversions conversions) {
//        conversionService.addConverter(new Converter<String, ObjectId>() {
//            @Override
//            public ObjectId convert(String source) {
//                throw new RuntimeException();
//            }
//        });
//    }
//    @Bean
//    public MappingMongoConverter mappingMongoConverter(MongoDbFactory factory, MongoMappingContext context, CustomConversions conversions) {
//        MappingMongoConverter converter = new MappingMongoConverter(new DefaultDbRefResolver(factory), context) {
//            @Override
//            public void afterPropertiesSet() {
//                conversions.registerConvertersIn(conversionService);
//            }
//        };
//        converter.setCustomConversions(conversions);
//        return converter;
//    }

}
