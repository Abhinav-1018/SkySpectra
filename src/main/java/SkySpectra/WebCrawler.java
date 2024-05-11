package SkySpectra;


import java.io.File;
import java.io.IOException;
import java.util.*;

public class WebCrawler {
//    public static searchFrequency.TreeNode root = new searchFrequency.TreeNode();
    private Set<String> visitedUrls;
    private Queue<String> urlsToVisit;
    private int maxUrlsToVisit;
    private String saveDir;

    public WebCrawler(int maxUrlsToVisit, String saveDir) {
        visitedUrls = new HashSet<String>();
        urlsToVisit = new LinkedList<String>();
        this.maxUrlsToVisit = maxUrlsToVisit;
        this.saveDir = saveDir;
    }

    public void clear() {
        visitedUrls.clear();
        urlsToVisit.clear();
    }

    public void crawl(String startingUrl, String saveDir) throws IOException {
        File dir = new File(saveDir);
        if (!dir.exists()) {
            dir.mkdir();
        }
        urlsToVisit.add(startingUrl);
        while (!urlsToVisit.isEmpty() && visitedUrls.size() < maxUrlsToVisit) {
            String url = urlsToVisit.poll();
            if (!visitedUrls.contains(url)) {
                visitedUrls.add(url);
                System.out.println("Visiting: " + url);
                String links = HTMLParser.parse(url, saveDir);
                for (String nextUrl : links.split(" ")) {
                    if (!visitedUrls.contains(nextUrl)) {
                        urlsToVisit.add(nextUrl);
                    }
                }
            }
        }
        System.out.println("Website is crawled!");
    }

    public static void main(String[] args) throws IOException {
        int maxUrlsToVisit = 30;
        String saveDir = "skySpectra";

        searchFrequency.TreeNode root = new searchFrequency.TreeNode();

        WebCrawler crawler = new WebCrawler(maxUrlsToVisit, saveDir);
        InvertedIndex index = new InvertedIndex();
        pageRanking pageRanking = new pageRanking();
        FrequencyCount freqCount = new FrequencyCount();

        Scanner indexScanner = new Scanner(System.in);
        Scanner scanner = new Scanner(System.in);
        int option;

        do {
            System.out.println("1: Do you want to get flight information by search ?");
            System.out.println("2: Crawl website");
            System.out.println("3: Inverted Indexing");
            System.out.println("4: Frequency Count");
            System.out.println("5: Page Ranking");
            System.out.println("6: Search Frequency");
            System.out.println("0: Terminate\n");

            System.out.println("Enter Value: ");
            option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1: SkySpectra.main();
                    break;
                case 2:
                    String startingUrl;
                    do {
                        System.out.print("Enter a starting URL: ");
                        startingUrl = scanner.nextLine();

                        if (!UrlValidator.validate(startingUrl)) {
                            System.out.println("Invalid URL. Please try again.");
                        }
                    } while (!UrlValidator.validate(startingUrl));
                    crawler.clear();
                    crawler.crawl(startingUrl, saveDir);
                    break;

                case 3:
                    index.buildIndex(saveDir);
                    System.out.println("Enter the keyword for inverted index: ");
                    String keyword = indexScanner.nextLine();

                    root.insert(keyword);
                    index.searchKeyword(keyword);
                    break;

                case 4:
                    System.out.print("Enter the keyword for frequency count(comma separted): ");
                    String keywords = indexScanner.nextLine();
                    for(String word: keywords.split(",")) {
                        root.insert(word);
                    }
                    freqCount.countFrequency(saveDir,keywords);
                    break;
                case 5:
                    System.out.print("Enter the keyword for page ranking(comma separted): ");
                    String keywords_ = indexScanner.nextLine();
                    for(String word: keywords_.split(",")) {
                        root.insert(word);
                    }
                    pageRanking.PageRank(saveDir,keywords_);
                    break;
                case 6:
                    searchFrequency s = new searchFrequency();
                    s.SearchFrequency(root);
                    break;
                case 0:
                    System.out.println("Thank you!");
                    break;

                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
        } while (option != 0  );
    }

}