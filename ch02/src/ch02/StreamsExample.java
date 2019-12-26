package ch02;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamsExample {

    public static void main(String[] args) throws IOException {
        Word[] array = {new Word("My", "RPR"),
                new Word("dog", "NN"),
                new Word("also", "RB"),
                new Word("likes", "VB"),
                new Word("eating", "VB"),
                new Word("sausage", "NN"),
                new Word(".", ".")};
        List<Word> list = Arrays.asList(array);

        // Filter only nouns
        List<String> nouns = list.stream()
                .filter(w -> "NN".equals(w.getPos()))
                .map(Word::getToken)
                .collect(Collectors.toList());
        System.out.println("Nouns:\n" + nouns);

        // Check how many unique POS tags in the stream
        Set<String> pos = list.stream()
                .map(Word::getPos)
                .collect(Collectors.toSet());
        System.out.println("Unique POS tags:\n" + pos);

        // Join a sequence of strings together
        String rawSentence = list.stream()
                .map(Word::getToken)
                .collect(Collectors.joining(" "));
        System.out.println("Joining strings:\n" + rawSentence);

        // Group words by POS tag
        Map<String, List<Word>> groupByPos = list.stream()
                .collect(Collectors.groupingBy(Word::getPos));
        System.out.println("Group by \"VB\":\n" + groupByPos.get("VB"));
        System.out.println("Group by \"NN\":\n" + groupByPos.get("NN"));

        // Get a map from tokens to the Word objects
        Map<String, Word> tokenToWord = list.stream()
                .collect(Collectors.toMap(Word::getToken, Function.identity()));
        System.out.println("Map from tokens to Word objects:\n" + tokenToWord);

        // Maximum length across all words in the sentence
        int maxTokenLength = list.stream()
                .mapToInt(w -> w.getToken().length())
                .max().getAsInt();
        System.out.println("Maximum length across all words in the " +
                "sentence:\n" + maxTokenLength);

        // Streams are easy to process in parallel because operations are
        // applied to each item separately.
        int[] firstLengths = list.parallelStream()
                .filter(w -> w.getToken().length() % 2 == 0)
                .map(Word::getToken)
                .mapToInt(String::length)
                .sequential()
                .sorted()
                .limit(2)
                .toArray();
        System.out.println("Top two elements:\n" +
                Arrays.toString(firstLengths));

        // Represent a text file as a stream of lines
        Path path = Paths.get("data/text.txt");
        try (Stream<String> lines = Files.lines(path, StandardCharsets.UTF_8)) {
            double average = lines
                    .flatMap(line -> Arrays.stream(line.split(" ")))
                    .map(String::toLowerCase)
                    .mapToInt(String::length)
                    .average().getAsDouble();
            System.out.println("Average token length: " + average);
        }
    }
}
