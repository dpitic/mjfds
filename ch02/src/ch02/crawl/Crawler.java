package ch02.crawl;

import ch02.UrlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.*;

/**
 * Web crawler used to retrieve URLs and saving their HTML code.  It will drop
 * pages that take longer to load than the specified timeout (seconds).
 */
public class Crawler implements AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Crawler.class);

    private final ExecutorService executor;
    private final int timeout;

    public Crawler(int timeout) {
        this.executor = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors());
        this.timeout = timeout;
    }

    public Optional<String> crawl(String url) throws IOException {
        try {
            Future<String> future = executor.submit(() -> UrlUtils.request(url));
            String result = future.get(timeout, TimeUnit.SECONDS);
            if (!result.isEmpty()) {
                return Optional.of(result);
            } else {
                LOGGER.info("Crawled empty result for {}", url);
                return Optional.empty();
            }
        } catch (TimeoutException e) {
            LOGGER.warn("Timeout exception: could not crawl {} in {} sec",
                    url, timeout);
            return Optional.empty();
        } catch (Exception e) {
            LOGGER.error("Unhandled exception during crawling", e);
            return Optional.empty();
        }
    }

    @Override
    public void close() throws Exception {
        executor.shutdown();
    }
}
