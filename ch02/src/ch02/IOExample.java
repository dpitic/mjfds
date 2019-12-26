package ch02;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class IOExample {

    public static void main(String[] args) throws IOException {
        System.out.println("Raw Java IO using BufferedReader:\n");
        rawJavaIOExample("data/text.txt", "rawIO.txt");

        System.out.println("\nUsing NIO interface:\n");
        bufferedReaderNIOExample("data/text.txt", "NIO.txt");

        System.out.println("\nUsing NIO File.readAllLines():\n");
        nioReadAllLinesExample("data/text.txt");
    }

    // Read a whole text file as a list of strings
    public static void rawJavaIOExample(String inFile, String outFile)
            throws IOException {
        List<String> lines = new ArrayList<>();

        // Read input file
        try (InputStream is = new FileInputStream(inFile)) {
            try (InputStreamReader isReader =
                         new InputStreamReader(is, StandardCharsets.UTF_8)) {
                try (BufferedReader reader = new BufferedReader(isReader)) {
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        lines.add(line);
                    }
                }
            }
        }
        System.out.println(lines);

        // Write to output file
        try (PrintWriter writer = new PrintWriter(outFile, "UTF-8")) {
            for (String line : lines) {
                String upperCase = line.toUpperCase(Locale.UK);
                writer.println(upperCase);
            }
        }
    }

    // Using NIO interface
    public static void bufferedReaderNIOExample(String inFile, String outFile)
            throws IOException {
        List<String> lines = new ArrayList<>();

        Path path = Paths.get(inFile);
        try (BufferedReader reader =
                     Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        System.out.println(lines);

        // Write to output file
        Path output = Paths.get(outFile);
        try (BufferedWriter writer = Files.newBufferedWriter(output,
                StandardCharsets.UTF_8)) {
            for (String line : lines) {
                String upperCase = line.toUpperCase(Locale.UK);
                writer.write(upperCase);
                writer.newLine();
            }
        }
    }

    // NIO read all lines
    public static void nioReadAllLinesExample(String inFile)
            throws IOException {
        Path path = Paths.get(inFile);
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        System.out.println(lines);
    }
}
