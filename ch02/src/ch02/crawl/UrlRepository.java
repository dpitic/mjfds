package ch02.crawl;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;

import java.util.Map;
import java.util.Optional;

/**
 * This class is used to store crawled HTML pages in a key-value store using
 * MapDB, which is a pure Java key-value store that implements the Map
 * interface.
 */
public class UrlRepository implements AutoCloseable {

    private final DB db;
    private final Map<String, String> map;


    public UrlRepository() {
        this.db = makeDb();
        this.map = createUrlMapDatabase(this.db);
    }

    /**
     * Put a url-html key-value pair into the map.
     *
     * @param url  corresponding to the HTML.
     * @param html document returned by the URL.
     */
    public void put(String url, String html) {
        map.put(url, html);
    }

    /**
     * Check whether the URL is in the map.
     *
     * @param url string to check.
     * @return True if the URL string is in the map; False otherwise.
     */
    public boolean contains(String url) {
        return map.containsKey(url);
    }

    /**
     * Get the HTML string corresponding to the URL key.
     *
     * @param url String key to look up the map.
     * @return String HTML if URL key exists; otherwise empty.
     */
    public Optional<String> get(String url) {
        if (map.containsKey(url)) {
            return Optional.of(map.get(url));
        } else {
            return Optional.empty();
        }
    }

    private static DB makeDb() {
        return DBMaker.fileDB("urls.db").closeOnJvmShutdown().make();
    }

    private static Map<String, String> createUrlMapDatabase(DB db) {
        HTreeMap<?, ?> hTreeMap = db.hashMap("urls").createOrOpen();
        Map<String, String> map = (Map<String, String>) hTreeMap;
        return map;
    }

    @Override
    public void close() throws Exception {
        db.close();
    }
}
