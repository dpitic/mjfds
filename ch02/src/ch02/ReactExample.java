package ch02;

import cyclops.async.LazyReact;
import cyclops.stream.FutureStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.jooq.lambda.tuple.Tuple2;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Cyclops React library extends the Java Streams API by dealing with data in
 * a functional way by adding new operations on streams and allows for more
 * control of the flow execution.  The library makes it easy to create parallel
 * stream for the iterator.
 */
public class ReactExample {
    // Extract all POS tags from the words text file and create a map that
    // associates each tag with a unique index.  Read the data using
    // LineIterator from Commons IO to process in parallel.
    public static void main(String[] args) throws IOException {
        LineIterator it = FileUtils.lineIterator(new File("data/words.txt"), "UTF-8");
        ExecutorService executor = Executors.newCachedThreadPool();
        FutureStream<String> stream =
                LazyReact.parallelBuilder().withExecutor(executor).from(it);

        Map<String, Integer> map = stream
                .map(line -> line.split("\t"))
                .map(arr -> arr[1].toLowerCase())
                .distinct()
                .zipWithIndex()
                .toMap(Tuple2::v1, t -> t.v2.intValue());

        System.out.println(map);
        executor.shutdown();
        it.close();
    }
}
