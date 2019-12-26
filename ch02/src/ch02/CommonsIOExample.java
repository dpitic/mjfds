package ch02;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CommonsIOExample {

    public static void main(String[] args) throws IOException {
        System.out.println("Commons FileUtils:");
        fileUtils("data/text.txt");

        System.out.println("\nCommons IOUtils:");
        ioUtils("data/text.txt");
    }

    // Using FileUtils to read the whole file
    private static void fileUtils(String filename) throws IOException {
        File textFile = new File(filename);
        // Read the whole file to a string
        String content = FileUtils.readFileToString(textFile,
                StandardCharsets.UTF_8);
        System.out.println("Commons FileUtils.readFileToString():\n" +
                content);
        // Read the whole file to a list of strings
        List<String> lines = FileUtils.readLines(textFile,
                StandardCharsets.UTF_8);
        System.out.println("Commons FileUtils.readLines():\n" + lines);
    }

    // Using IOUtils
    private static void ioUtils(String filename) throws IOException {
        // Read whole file to a string
        try (InputStream is = new FileInputStream(filename)) {
            String content = IOUtils.toString(is, StandardCharsets.UTF_8);
            System.out.println("IOUtils.toString():\n" + content);
        }

        // Read whole file to list of strings
        try (InputStream is = new FileInputStream(filename)) {
            List<String> lines = IOUtils.readLines(is, StandardCharsets.UTF_8);
            System.out.println("IOUtils.readLines():\n" + lines);
        }
    }
}
