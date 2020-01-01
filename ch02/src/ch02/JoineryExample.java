package ch02;

import cyclops.async.LazyReact;
import joinery.DataFrame;
import org.jooq.lambda.tuple.Tuple2;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JoineryExample {

    public static void main(String[] args) throws IOException {
        final String file = "data/csv-example-generatedata_com.csv";
        DataFrame<Object> df = DataFrame.readCsv(file);

        // Get the 'country' column
        List<Object> country = df.col("country");
        // Associate every country with a unique index
        Map<String, Long> map = LazyReact.sequentialBuilder()
                .from(country)
                .cast(String.class)
                .distinct()
                .zipWithIndex()
                .toMap(Tuple2::v1, Tuple2::v2);

        // Build unique index for each unique country
        List<Object> indexes = country.stream()
                .map(map::get).collect(Collectors.toList());
        df = df.drop("country");
        df.add("country_index", indexes);

        System.out.println(df);
    }
}
