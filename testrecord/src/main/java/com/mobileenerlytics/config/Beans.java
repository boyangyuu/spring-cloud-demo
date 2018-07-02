package com.mobileenerlytics.config;

import com.mobileenerlytics.util.DBOperation;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class Beans {

    @Autowired
    private DbProperties dbProperties;

    @Bean
    public RestTemplate restTemplate() {return new RestTemplate();}

    @Bean
    public DBOperation dbOperation() {
        return new DBOperation();
    }

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
