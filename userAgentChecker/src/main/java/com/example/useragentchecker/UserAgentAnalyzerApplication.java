package com.example.useragentchecker;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.UserAgent;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class UserAgentAnalyzerApplication {

    public static void main(String[] args) {
        try {
            // Load and parse the JSON file from src/main/resources
            ObjectMapper objectMapper = new ObjectMapper();
            InputStream inputStream = UserAgentAnalyzer.class.getClassLoader().getResourceAsStream("userAgents.json");
            if (inputStream == null) {
                throw new RuntimeException("Could not find userAgents.json file in resources");
            }

            List<Map<String, String>> userAgentList = objectMapper.readValue(
                    inputStream,
                    new TypeReference<List<Map<String, String>>>() {}
            );

            // Analyze each user-agent string
            for (Map<String, String> entry : userAgentList) {
                String userAgentString = entry.get("userAgent");
                UserAgent userAgent = UserAgent.parseUserAgentString(userAgentString);

                // Determine if the user-agent is a bot
                boolean isBot = isBot(userAgent);

                System.out.println("User-Agent: " + userAgentString);
                System.out.println("Is Bot: " + isBot);
                System.out.println("----");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isBot(UserAgent userAgent) {
        Browser browser = userAgent.getBrowser();

        // Check if the browser name or group name contains indicators of bots
        return browser.getName().contains("Bot") ||
                browser.getGroup().getName().equalsIgnoreCase("Bot") ||
                browser.getName().contains("Spider") ||
                browser.getGroup().getName().equalsIgnoreCase("Crawler") ||
                browser.getGroup().getName().contains("");
    }
}
