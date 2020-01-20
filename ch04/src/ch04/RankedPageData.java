package ch04;

import ch02.BeanToJoinery;
import ch04.cv.Dataset;
import ch04.cv.Split;
import com.fasterxml.jackson.jr.ob.JSON;
import com.google.common.base.Throwables;
import joinery.DataFrame;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility class used to parse search engine ranked page data.
 */
public class RankedPageData {

    /**
     * Return a list of RankedPage objects from the specified file.
     *
     * @param filename JSON file to parse.
     * @return List of RankedPage objects.
     * @throws IOException if the file could not be read.
     */
    public static List<RankedPage> readRankedPages(String filename) throws
            IOException {
        Path path = Paths.get(filename);
        try (Stream<String> lines = Files.lines(path)) {
            return lines.map(line -> parseJson(line))
                    .collect(Collectors.toList());
        }
    }

    /**
     * Parse a string of JSON and return a RankedPage object.
     *
     * @param line String of JSON to parse.
     * @return RankedPage object.
     */
    public static RankedPage parseJson(String line) {
        try {
            return JSON.std.beanFrom(RankedPage.class, line);
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    /**
     * Read file and return a train/test split dataset.
     *
     * @param filename  JSON data file to read.
     * @param testRatio train/test split test ratio.
     * @return train/test split Dataset.
     * @throws IOException if data file cannot be read.
     */
    public static Split readRankedPagesMatrix(String filename,
                                              double testRatio) throws
            IOException {
        List<RankedPage> pages = RankedPageData.readRankedPages(filename);
        DataFrame<Object> dataFrame = BeanToJoinery.convert(pages,
                RankedPage.class);

        List<Object> page = dataFrame.col("page");
        double[] target = page.stream().mapToInt(o -> (int) o)
                .mapToDouble(p -> (p == 0) ? 1.0 : 0.0).toArray();

        dataFrame = dataFrame.drop("page", "url", "position");
        double[][] X = dataFrame.toModelMatrix(0.0);

        Dataset dataset = new Dataset(X, target);
        return dataset.trainTestSplit(testRatio);
    }
}
