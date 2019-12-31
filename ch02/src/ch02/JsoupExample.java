package ch02;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Extract the participant performance from Kaggle.com leader board.  This
 * example demonstrates the use of the Jsoup library to parse HTML documents.
 * The information is in a table and in order to extract the data you need to
 * find an anchor that uniquely points to the table.
 */
public class JsoupExample {

    public static void main(String[] args) throws IOException {
        Map<String, Double> result = new HashMap<>();

        String rawHtml = UrlUtils.request("https://www.kaggle.com/c/avito-duplicate-ads-detection/leaderboard");
        Document document = Jsoup.parse(rawHtml);
        Elements tableRows = document.select("table.competition-leaderboard__table > tbody > tr");
        for (Element tr : tableRows) {
            Elements columns = tr.select("td");
            if (columns.isEmpty()) {
                continue;
            }

            String team = columns.get(2).select("a.team-link").text();
            double score = Double.parseDouble(columns.get(3).text());
            result.put(team, score);
        }

        Comparator<Map.Entry<String, Double>> byValue =
                Map.Entry.comparingByValue();
        result.entrySet().stream()
                .sorted(byValue.reversed())
                .forEach(System.out::println);
    }
}
