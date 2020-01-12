package ch03;

import ch02.crawl.RankedPage;
import com.fasterxml.jackson.jr.ob.JSON;
import com.google.common.base.Throwables;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Parse a file with search engine ranked pages in JSON format.
 */
public class Data {

    /**
     * Return a list of ranked pages from the specified file.
     *
     * @param filename search engine ranked pages JSON file.
     * @return List of RankedPage objects.
     * @throws IOException
     */
    public static List<RankedPage> readRankedPages(String filename) throws
            IOException {
        Path path = Paths.get(filename);
        try (Stream<String> lines = Files.lines(path)) {
            return lines.map(line ->
                    parseJson(line)).collect(Collectors.toList());
        }
    }

    /**
     * Parse a JSON line of ranked pages and convert to RankedPage class.
     *
     * @param line of JSON to parse.
     * @return RankedPage object of the parsed JSON line.
     */
    public static RankedPage parseJson(String line) {
        try {
            return JSON.std.beanFrom(RankedPage.class, line);
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }
}
