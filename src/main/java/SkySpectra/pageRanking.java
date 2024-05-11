package SkySpectra;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

public class pageRanking {

    // Inner class to represent a web page
    class Page {
        String WebPagename; // to name the web page we are ranking
        int score; // to store the scores of the web page

        // Defining Constructor to store the name and score
        Page(String name, int score) {
            this.WebPagename = name;
            this.score = score;
        }
    }

    public void PageRank(String filePath,String input) {
        // The local directory that contains the web pages
        File folder = new File(filePath);
        // To get all files from the directory
        File[] listOfFiles = folder.listFiles();

        Scanner scanner = new Scanner(System.in);
        //Defining Array List
        List<String> keywords = new ArrayList<String>();
        while (keywords.isEmpty()) {

            for (String keyword : input.split(",")) {
                keywords.add(keyword.trim().toLowerCase());
            }
            if (keywords.isEmpty()) {
                System.out.println("Please enter at least one keyword.");
            }
        }
        System.out.println(keywords);

        // A Map to store the frequency of each keyword that is mentioned above
        Map<String, Integer> keywordFrequencies = new HashMap<>();

        // To initialize the 0 frequency for each of the keyword
        for (String keyword : keywords) {
            keywordFrequencies.put(keyword, 0);
        }

        // Initialize a Priority queue to store the web pages as per their respective
        // scores
        PriorityQueue<pageRanking.Page> heap = new PriorityQueue<>(10, (p1, p2) -> Integer.compare(p2.score, p1.score));
        // To loop through each web page
        for (File file : listOfFiles) {
            // Checker to check if the file is in .htm format
            if (file.getName().endsWith(".txt")) {
                int score = 0;
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
                                // To increase the score and frequency of the keyword
                                score += keywordFrequencies.getOrDefault(word, 0) + 1;
                                keywordFrequencies.put(word, keywordFrequencies.getOrDefault(word, 0) + 1);
                            }
                        }
                    }
                    // Reset the frequency of each keyword to 0
                    for (String keyword : keywords) {
                        keywordFrequencies.put(keyword, 0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // To create a page object with the name and score of the file
                pageRanking.Page page = new pageRanking.Page(file.getName(), score);
                // To add the page object to the earlier made priority queue
                heap.offer(page);
            }
        }
        // To print the top 10 web pages based on keyword matches
        System.out.println("Top 10 web pages based on keyword matches: \n");
        List<pageRanking.Page> topPages = new ArrayList<>();
        for (int i = 1; i < 11; i++) {
            if (heap.isEmpty()) {
                break;
            }
            pageRanking.Page page = heap.poll();
            topPages.add(page);
            System.out.println((i + ". " + page.WebPagename + " - score: " + page.score));
        }
    }
}
