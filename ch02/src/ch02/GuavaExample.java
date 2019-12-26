package ch02;

import com.google.common.collect.*;
import com.google.common.io.CharSource;
import com.google.common.io.Files;
import com.google.common.primitives.Ints;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class GuavaExample {

    public static void main(String[] args) throws IOException {
        // CharSource is an abstraction for any source of character based data
        File file = new File("data/words.txt");
        CharSource wordsSource = Files.asCharSource(file, StandardCharsets.UTF_8);
        List<String> lines = wordsSource.readLines();
        System.out.println("List of lines:\n" + lines);

        // Lists.transform() applies a function to each element in the list
        List<Word> words = Lists.transform(lines, line -> {
            String[] split = line.split("\t");
            return new Word(split[0].toLowerCase(), split[1]);
        });
        System.out.println("\nList of words:\n" + words);

        // Multiset is a set where the same element can be stored multiple times
        // and are usually used for counting things.
        Multiset<String> pos = HashMultiset.create();
        for (Word word : words) {
            pos.add(word.getPos());
        }
        // Output the result sorted by counts
        Multiset<String> sortedPos = Multisets.copyHighestCountFirst(pos);
        System.out.println("\nSorted by counts:\n" + sortedPos);

        // Multimap is a map where each key can have multiple values
        ArrayListMultimap<String, String> wordsByPos =
                ArrayListMultimap.create();
        // Average length per POS tag
        for (Word word : words) {
            wordsByPos.put(word.getPos(), word.getToken());
        }
        // View a multimap as a map of collections
        Map<String, Collection<String>> wordsByPosMap = wordsByPos.asMap();
        System.out.println("\nView a multimap as a map of collections:\n");
        wordsByPosMap.entrySet().forEach(System.out::println);

        // Table collection can be seen as a 2D extension of the map interface
        // where each entry is indexed by two keys, row keys and column keys.
        Table<String, String, Integer> table = HashBasedTable.create();
        for (Word word : words) {
            Integer cnt = table.get(word.getPos(), word.getToken());
            if (cnt == null) {
                cnt = 0;
            }
            table.put(word.getPos(), word.getToken(), cnt + 1);
        }
        // Rows and columns can be accessed individually
        Map<String, Integer> nouns = table.row("NN");
        System.out.println("\nNouns:\n" + nouns);

        String word = "eu";
        Map<String, Integer> posTags = table.column(word);
        System.out.println("Look for word 'eu':\n" + posTags);

        // Convert a collection of primitive wrappers to a primitive array
        Collection<Integer> values = nouns.values();
        int[] nounCounts = Ints.toArray(values);
        int totalNounCount = Arrays.stream(nounCounts).sum();
        System.out.println("\nTotal noun count: " + totalNounCount);

        // Nice abstraction for sorting data: Ordering, which extends the std
        // Comparator interface
        Ordering<Word> byTokenLength =
                Ordering.natural().<Word>onResultOf(w ->
                        w.getToken().length()).reverse();
        List<Word> sortedByLength = byTokenLength.immutableSortedCopy(words);
        System.out.println("\nSorted by token length:\n" + sortedByLength);

        // Ordering implements the Comparator interface, so it can be used
        // wherever a comparator is expected
        List<Word> sortedCopy = new ArrayList<>(words);
        Collections.sort(sortedCopy, byTokenLength);
        System.out.println("\nSorted by token length:\n" + sortedCopy);

        // Extract first and last 10 elements
        List<Word> first10 = byTokenLength.leastOf(words, 10);
        System.out.println("\nFirst 10 words:\n" + first10);
        List<Word> last10 = byTokenLength.greatestOf(words, 10);
        System.out.println("\nLast 10 words:\n" + last10);
    }
}
