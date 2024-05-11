package SkySpectra;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

public class FrequencyCount {

    public void countFrequency(String filePath, String words) {
        // The local directory that contains the web pages
        File folder = new File(filePath);
        // To get all files from the directory
        File[] listOfFiles = folder.listFiles();

        Scanner scanner = new Scanner(System.in);
        List<String> keywords = new ArrayList<String>();
        while (keywords.isEmpty()) {

            for (String keyword : words.split(",")) {
                keywords.add(keyword.trim().toLowerCase());
            }
            if (keywords.isEmpty()) {
                System.out.println("Please enter at least one keyword.");
            }
        }
        System.out.println(keywords);

        // A Map to store the frequency of each web page that contains the keyword
        Map<String, Integer> pageFrequencies = new HashMap<>();

        // To initialize the 0 frequency for each of the web page
        for (File file : listOfFiles) {
            // Checker to check if the file is in .htm format
            if (file.getName().endsWith(".txt")) {
                pageFrequencies.put(file.getName(), 0);
            }
        }

        // To loop through each web page
        for (File file : listOfFiles) {
            // Checker to check if the file is in .htm format
            if (file.getName().endsWith(".txt")) {
                // to read the contents of the file
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    // To read each line of the file
                    while ((line = reader.readLine()) != null) {
                        // To Tokens the line into words
                        StringTokenizer tokenizer = new StringTokenizer(line);
                        while (tokenizer.hasMoreTokens()) {
                            String word = tokenizer.nextToken().toLowerCase().replaceAll("[^a-z0-9]+", "");

                            // To check if the word is a keyword
                            if (keywords.contains(word)) {
                                // To increase the frequency of the web page
                                pageFrequencies.put(file.getName(), pageFrequencies.get(file.getName()) + 1);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        // To print the web pages and their frequencies based on keyword matches
        System.out.println("Web pages and their frequencies based on keyword matches: \n");
        for (String page : pageFrequencies.keySet()) {
            int frequency = pageFrequencies.get(page);
            if (frequency > 0) {
                System.out.println(page + " - frequency: " + frequency);
            }
            else {
                System.out.println(page + " - frequency: " + frequency);
            }
        }
    }
}
