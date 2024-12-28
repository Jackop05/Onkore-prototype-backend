package com.onkore_backend.onkore.Config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class MongoConfig {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Bean
    public MongoClient mongoClient() {
        try {
            MongoClient mongoClient = MongoClients.create(mongoUri);
            MongoDatabase database = mongoClient.getDatabase("Onkore_main_database");

            List<String> collectionsFound = new ArrayList<>();
            for (String collectionName : database.listCollectionNames()) {
                collectionsFound.add(collectionName);
            }

            if (collectionsFound.isEmpty()) {
                System.out.println("\nConnected to database: " + database.getName() + "\nNo collections found in the database.\n");
            } else {
                System.out.println("\nConnected to database: " + database.getName());
                collectionsFound.forEach(collection -> System.out.println("Collection found: " + collection));
                System.out.println("\n");
            }

            return mongoClient;
        } catch (Exception e) {
            System.err.println("\nFailed to connect to database: " + e.getMessage() + "\n");
            throw e;
        }
    }
}


