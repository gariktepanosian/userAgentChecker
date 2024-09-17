package com.example.useragentchecker;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SQLToHTMLExtractor {
    public static void main(String[] args) {
        getHtmlContent();

//        getUpdateQueryWithoutHtmlContent();
    }

    private static void getUpdateQueryWithoutHtmlContent() {
        // Path to your input SQL file
        String inputFilePath = "src/main/resources/UPDATE email_templates";

        // Regular expression to match the UPDATE statement and capture the type
        String regex = "UPDATE email_templates\\s+SET content = '.+?'\\s+WHERE type = '(.+?)';";
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);

        try {

            // Read all content from the input file
            String fileContent = new String(Files.readAllBytes(Paths.get(inputFilePath)));

            // Match the regex pattern in the file content
            Matcher matcher = pattern.matcher(fileContent);

            // Define the output SQL file path
            String outputFilePath = "src/main/resources/sqls/UPDATE_email_templates_no_content.sql";

            // Create a writer for the output file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
                // Iterate through all matches
                while (matcher.find()) {
                    // Extract the type
                    String type = matcher.group(1);

                    // Write the modified UPDATE statement without content
                    String updateQuery = "UPDATE email_templates SET content = '' WHERE type = '" + type + "';";
                    writer.write(updateQuery);
                    writer.newLine();
                }
                System.out.println("SQL file created without content: " + outputFilePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void getHtmlContent() {
        // Path to your input SQL file
        String inputFilePath = "src/main/resources/oldFiles.txt";

        // Regular expression to extract the content from the UPDATE statement
        String regex = "UPDATE email_templates\\s+SET content = '(.+?)'\\s+WHERE type = '(.+?)';";
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);

        try {
            // Read all content from the input file
            String fileContent = new String(Files.readAllBytes(Paths.get(inputFilePath)));

            // Match the regex pattern in the file content
            Matcher matcher = pattern.matcher(fileContent);

            // Counter to keep track of the file number
            int fileCounter = 1;

            // Iterate through all matches
            while (matcher.find()) {
                // Extract the content and type
                String content = matcher.group(1).replace("\\'", "'");
                String type = matcher.group(2);

                // Define the output HTML file path
                String outputFilePath = "src/main/resources/oldFiles/output_" + type + "_" + fileCounter + ".html";

                // Write the content to a new HTML file
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
                    writer.write(content);
                    System.out.println("HTML file created: " + outputFilePath);
                }

                // Increment the file counter
                fileCounter++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

