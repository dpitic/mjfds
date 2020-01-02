package ch02.crawl;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

/**
 * Read Bing search URLs from the specified file, download their HTML and save
 * it to the map.
 */
public class BingCrawlerExample {

    private static final Logger LOGGER = LoggerFactory.getLogger(
            BingCrawlerExample.class);

    public static void main(String[] args) throws Exception {
        try (Crawler crawler = new Crawler(10)) {
            try (UrlRepository urls = new UrlRepository()) {
                crawl(crawler, urls);
            }
        }
    }

    /**
     * Crawl the URLs in the Bing search results file, download their HTML and
     * save it to the map.
     *
     * @param crawler Crawler used to crawl the URLs.
     * @param urls    URL repository.
     * @throws IOException
     */
    private static void crawl(Crawler crawler, UrlRepository urls)
            throws IOException {
        Path path = Paths.get("data/bing-search-results.txt");
        List<String> lines = FileUtils.readLines(path.toFile(),
                StandardCharsets.UTF_8);

        lines.parallelStream()
                .map(line -> line.split("\t"))
                .map(split -> split[3])
                .distinct()
                .filter(url -> !urls.contains(url))
                .forEach(url -> {
                    try {
                        Optional<String> html = crawler.crawl(url);
                        if (html.isPresent()) {
                            LOGGER.debug("Successfully crawled {}", url);
                            urls.put(url, html.get());
                        }
                    } catch (Exception e) {
                        LOGGER.error("Exception processing url {}", url, e);
                    }
                });

        LOGGER.info("Done");
    }
}
