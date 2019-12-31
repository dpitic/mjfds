package ch02;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CommonsCSVExample {

    public static void main(String[] args) throws IOException {
        // Display contents of CSV file
        List<Person> csv = csvExample(
                "data/csv-example-generatedata_com.csv");
        System.out.println("CSV file:\n" + csv);

        // Display contents of TSV file
        List<Person> tsv = tsvExample(
                "data/tsv-example-generatedata_com.tsv");
        System.out.println("\nTSV file:\n" + tsv);
    }

    // Read CSV file into list of Person records
    public static List<Person> csvExample(String filename) throws IOException {
        List<Person> result = new ArrayList<>();

        Path csvFile = Paths.get(filename);
        try (BufferedReader reader = Files.newBufferedReader(csvFile,
                StandardCharsets.UTF_8)) {
            CSVFormat csv = CSVFormat.RFC4180.withHeader();
            try (CSVParser parser = csv.parse(reader)) {
                // Iterator is useful when CSV file is too large to fit into
                // memory
                Iterator<CSVRecord> it = parser.iterator();
                it.forEachRemaining(rec -> {
                    String name = rec.get("name");
                    String email = rec.get("email");
                    String country = rec.get("country");
                    int salary =
                            Integer.parseInt(rec.get("salary").substring(1));
                    int experience = Integer.parseInt(rec.get("experience"));
                    Person person = new Person(name, email, country, salary,
                            experience);
                    result.add(person);
                });
            }
        }
        return result;
    }

    // Read TSV file into list of Person records
    public static List<Person> tsvExample(String filename) throws IOException {
        List<Person> result = new ArrayList<>();

        Path tsvFile = Paths.get(filename);
        try (BufferedReader reader = Files.newBufferedReader(tsvFile, StandardCharsets.UTF_8)) {
            CSVFormat tsv = CSVFormat.TDF.withHeader();
            try (CSVParser parser = tsv.parse(reader)) {
                Iterator<CSVRecord> it = parser.iterator();
                it.forEachRemaining(rec -> {
                    String name = rec.get("name");
                    String email = rec.get("email");
                    String country = rec.get("country");
                    int salary =
                            Integer.parseInt(rec.get("salary").substring(1));
                    int experience = Integer.parseInt(rec.get("experience"));
                    Person person = new Person(name, email, country, salary,
                            experience);
                    result.add(person);
                });
            }
        }
        return result;
    }
}
