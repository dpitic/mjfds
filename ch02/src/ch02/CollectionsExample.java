package ch02;

import java.util.*;

public class CollectionsExample {

    public static void main(String[] args) {

        // List interface provides access to elements by their index
        List<String> list = new ArrayList<>();
        list.add("alpha");
        list.add("beta");
        list.add("beta");
        list.add("gamma");
        System.out.println(list);

        // Set allow unique elements only
        Set<String> set = new HashSet<>();
        set.add("alpha");
        set.add("beta");
        set.add("beta");
        set.add("gamma");
        System.out.println(set);

        // Iterable interface enables for-each loop to iterate each element
        for (String el : set) {
            System.out.println(el);
        }

        // Map interface maps keys to values; dictionary or associative array
        Map<String, String> map = new HashMap<>();
        map.put("alpha", "α");
        map.put("beta", "β");
        map.put("gamma", "γ");
        System.out.println(map);

        // Collections provides helper methods
        String max = Collections.min(list);
        String min = Collections.max(list);
        System.out.println("min: " + min + ", max: " + max);
        Collections.sort(list);
        Collections.shuffle(list);
    }
}
