package com.example.useragentchecker;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.io.IOException;

public class WebScraperSelenium {

    public static void main(String[] args) {
        // Path to your ChromeDriver
//        System.setProperty("webdriver.chrome.driver", "C:\\webdrivers\\chromedriver.exe");
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();

        try {
            // Load the webpage
            driver.get("https://www.browserscan.net/user-agent");

            // Wait for the page to load (you might need to add more sophisticated waiting)
            Thread.sleep(2000);

            ObjectMapper mapper = new ObjectMapper();
            File jsonFile = new File("src/main/resources/userAgents.json"); // Replace with the path to your JSON file
            JsonNode rootNode = mapper.readTree(jsonFile);

            ArrayNode resultsArray = mapper.createArrayNode();

            // Iterate over each "useragent" value
            for (JsonNode userAgentNode : rootNode) {
                String userAgent = userAgentNode.get("userAgent").asText();

                WebElement deleteInputButton = driver.findElement(By.className("_o2lsz1"));
                deleteInputButton.click();

                // Find the input field and paste your text
                WebElement inputField = driver.findElement(By.className("_13lc8mv"));
                inputField.sendKeys(userAgent);

                // Find the search button and click it
                WebElement searchButton = driver.findElement(By.className("_8l8067"));
                searchButton.click();

                // Wait for the results to load
                Thread.sleep(2000);

                // Extract the results
                WebElement resultElement = driver.findElement(By.xpath("(//div[@class='_180ig0x']//div[@class='_hrtekd']/p)[2]"));
                String resultText = resultElement.getText();


                ObjectNode resultObject = mapper.createObjectNode();
                resultObject.put("userAgent", userAgent);
                resultObject.put("result", resultText);

                // Add the result object to the results array
                resultsArray.add(resultObject);
            }

            // Create a root object to hold the results array
            ObjectNode rootObject = mapper.createObjectNode();
            rootObject.set("results", resultsArray);

            // Write the results to a new JSON file
            File outputJsonFile = new File("src/main/resources/userAgentResults.json");
            mapper.writerWithDefaultPrettyPrinter().writeValue(outputJsonFile, rootObject);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            // Close the browser
            driver.quit();
        }
    }
}

