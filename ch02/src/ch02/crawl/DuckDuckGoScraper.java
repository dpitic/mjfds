package ch02.crawl;

import ch02.UrlUtils;
import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Extract HTML content from DuckDuckGo Search.
 */
public class DuckDuckGoScraper {

    private static final Logger LOGGER = LoggerFactory.getLogger(
            DuckDuckGoScraper.class);

    public List<DuckDuckGoPage> crawl(String query) throws IOException {
        final String url = "https://duckduckgo.com/lite?q="
                + query.toLowerCase().replace(' ', '+');

        List<DuckDuckGoPage> results = Lists.newArrayListWithCapacity(30);
        String html = UrlUtils.userAgentRequest(url);
        Document document = Jsoup.parse(html);
        Elements links = document.select(
                "tr:not(.result-sponsored) a.result-link");
        int position = 1;
        for (Element link : links) {
            results.add(new DuckDuckGoPage(query, position,
                    link.attr("href")));
            position++;
        }
        return results;
    }

    public static class DuckDuckGoPage {
        private final String query;
        private final int position;
        private final String url;

        public DuckDuckGoPage(String query, int position, String url) {
            this.query = query;
            this.position = position;
            this.url = url;
        }

        public String getQuery() {
            return query;
        }

        public int getPosition() {
            return position;
        }

        public String getUrl() {
            return url;
        }

        @Override
        public String toString() {
            return "[query=" + query + ", position=" + position + ", url="
                    + url + "]";
        }
    }

    public static void main(String[] args)
            throws IOException, InterruptedException {
        DuckDuckGoScraper scraper = new DuckDuckGoScraper();

        List<String> queries = FileUtils.readLines(
                new File("data/keywords.txt"), StandardCharsets.UTF_8);
        try (PrintWriter pw = new PrintWriter("duckduckgo-search-results.txt")) {
            for (String query :
                    queries) {
                LOGGER.info("Crawling {}", query);
                List<DuckDuckGoPage> crawl = scraper.crawl(query);
                for (DuckDuckGoPage page :
                        crawl) {
                    pw.print(page.getQuery());
                    pw.print('\t');
                    pw.print(page.getPosition());
                    pw.print('\t');
                    pw.print(page.getUrl());
                    pw.println();
                }
                pw.flush();

                int sleepTime = RandomUtils.nextInt(1000, 3000);
                LOGGER.info("Sleeping {} ms", sleepTime);
                Thread.sleep(sleepTime);
            }
        }
    }
}
