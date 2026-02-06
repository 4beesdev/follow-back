package rs.oris.back.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MongoServerConfig {

    public static String MONGO_SERVER_URI = "";

    @Value("${oris.mongo.base.url:http://follow-gps-data:8080}")
    private String mongoBaseUrl;

    public String getMongoBaseUrl() {
        return mongoBaseUrl;
    }
}
