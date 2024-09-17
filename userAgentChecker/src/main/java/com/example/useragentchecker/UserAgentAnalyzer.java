package com.example.useragentchecker;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.bitwalker.useragentutils.UserAgent;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class UserAgentAnalyzer {

    // Lists of known bots and scrapers for classification
    private static final List<String> BOTS = List.of("Googlebot", "Bingbot", "Slurp", "DuckDuckBot");
    private static final List<String> SCRAPERS = List.of("Scrapy", "python-requests");

    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        File jsonFile = new File("user_agents.json");

        try {
            // Read JSON file
            List<Map<String, String>> userAgentsList = mapper.readValue(jsonFile, new TypeReference<>() {});

            // Analyze each user agent
            for (Map<String, String> entry : userAgentsList) {
                String userAgentString = entry.get("userAgent");
                String type = classifyUserAgent(userAgentString);
                System.out.println("User Agent: " + userAgentString);
                System.out.println("Software Type: " + type);
                System.out.println();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String classifyUserAgent(String userAgentString) {
        UserAgent userAgent = new UserAgent(userAgentString);

        // Check if it's a bot
        if (BOTS.stream().anyMatch(bot -> userAgentString.contains(bot))) {
            return "crawler";
        }

        // Check if it's a scraper
        if (SCRAPERS.stream().anyMatch(scraper -> userAgentString.contains(scraper))) {
            return "crawler";
        }

        // Check if it's a browser (in-app-browser in your case)
        if (userAgent.getBrowser().getName() != null && !userAgent.getBrowser().getName().isEmpty()) {
            return "in-app-browser";
        }

        // Default to unknown type if none of the conditions are met
        return "unknown";
    }
}

