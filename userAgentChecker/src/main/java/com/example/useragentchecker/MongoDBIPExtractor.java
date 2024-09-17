package com.example.useragentchecker;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.List;

public class MongoDBIPExtractor {

    public static void main(String[] args) {
        // MongoDB connection details
        String username = "mongodb";
        String password = "aelohDoch9yaegha";  // Replace with your actual password
        String databaseName = "visitors";  // Replace with your actual database name
        String collectionName = "visitors";  // Replace with your actual collection name
        String authDatabase = "admin";  // The authentication database
        String host = "149.28.44.179";
        int port = 27017;

        // Connection string with authentication
        String connectionString = String.format("mongodb://%s:%s@%s:%d/%s?authSource=%s",
                username, password, host, port, databaseName, authDatabase);
        String outputFilePath = "src/main/resources/ip_addresses.txt";
        System.out.println("Connection String " + connectionString);
        // Connect to MongoDB
        int batchSize = 2000;

        Date sevenDaysAgo = Date.from(Instant.now().minusSeconds(7 * 24 * 60 * 60));

        // Create the filter for documents with "changedAt" within the last 7 days
        Bson dateFilter = Filters.gte("changedAt", sevenDaysAgo);

        // Connect to MongoDB
        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            System.out.println("Connected to MongoDB");

            MongoDatabase database = mongoClient.getDatabase(databaseName);
            MongoCollection<Document> collection = database.getCollection(collectionName);

            // Output file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {

                int totalFetched = 0;
                while (true) {
                    FindIterable<Document> documents = collection.find(dateFilter)
                            .skip(totalFetched)
                            .limit(batchSize);

                    int currentBatchSize = 0;
                    for (Document doc : documents) {
                        String ipAddress = doc.getString("ipAddress");
                        if (ipAddress != null) {
                            writer.write(ipAddress);
                            writer.newLine();
                            System.out.println("Extracted IP: " + ipAddress);
                        }
                        currentBatchSize++;
                    }

                    totalFetched += currentBatchSize;
                    System.out.println("Fetched " + currentBatchSize + " documents in this batch. Total fetched: " + totalFetched);

                    // Break the loop if less than batchSize documents were fetched, indicating the end of the collection
                    if (currentBatchSize < batchSize) {
                        break;
                    }
                }

                System.out.println("IP addresses extracted successfully to " + outputFilePath);

            } catch (IOException e) {
                System.err.println("Error writing to file: " + e.getMessage());
            }

        } catch (Exception e) {
            System.err.println("Error connecting to MongoDB: " + e.getMessage());
        }
    }
}

