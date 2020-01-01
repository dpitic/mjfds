package ch02;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

public class JsoupExample {

    public static void main(String[] args) throws IOException {
        System.out.println("Kaggle example:");
        kaggle();  // currently broken

        System.out.println("\nLotto data URLs:");
        lotto();
    }

    /**
     * Extract the participant performance from Kaggle.com leader board.  This
     * example demonstrates the use of the Jsoup library to parse HTML
     * documents. The information is in a table and in order to extract the
     * data you need to find an anchor that uniquely points to the table.
     */
    private static void kaggle() throws IOException {
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

    /**
     * Extract the URLs from an unordered list.
     *
     * @throws IOException
     */
    private static void lotto() throws IOException {
        List<String> result = new ArrayList<>();

        final String rawHtml = UrlUtils.request("https://www.lotterywest.wa.gov.au/results/frequency-charts");
        final Document document = Jsoup.parse(rawHtml);
        Elements listItems = document.select("ul.lw-freqchart-list > li");
        for (Element li : listItems) {
            String href = li.select("a").first().attr("abs:href");
            result.add(href);
        }

        result.stream().forEach(System.out::println);
    }
}
