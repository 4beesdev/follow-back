package rs.oris.back.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MongoServerConfig {

    public static String MONGO_SERVER_URI = "";

    @Value("${oris.mongo.base.url:http://142.93.164.78:8080}")
    private String mongoBaseUrl;

    public String getMongoBaseUrl() {
        return mongoBaseUrl;
    }
}
