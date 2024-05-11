package SkySpectra;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class HTMLParser {

    public static String parse(String url, String saveDir) throws IOException {
        Document doc = Jsoup.connect(url).get();
        String filename = saveDir + "/" + url.replaceAll("[^a-zA-Z0-9.-]", "_") + ".txt";
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        writer.write(doc.text());
        writer.close();
        Elements links = doc.select("a[href]");
        String linksString = "";
        for (Element link : links) {
            String nextUrl = link.absUrl("href");
            //Traverse till all links are visited
            if (!nextUrl.isEmpty()) {
                linksString += nextUrl + " ";
            }
        }
        return linksString.trim();
    }
}
