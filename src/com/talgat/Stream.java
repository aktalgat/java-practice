package com.talgat;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Stream {

    public static void main(String[] args) {
        List<Integer> list = List.of(1, 2, 3, 4, -1, -12);
        List<Integer> list2 = list.stream().filter((p) -> p != -1)
                .sorted().peek(System.out::println)
                .collect(Collectors.toList());
        System.out.println(list2);

        List<String> collection = Arrays.asList("a1", "a2", "a3", "a4");
        System.out.println(collection.stream().filter("a1"::equals).count());

        System.out.println(collection.stream().findFirst().orElse("0"));

        System.out.println(collection.stream().findAny().orElse("0"));

        System.out.println(collection.stream().skip(collection.size() - 1).findAny().orElse("empty"));

        System.out.println(collection.stream().skip(2).findFirst().get());

        System.out.println(collection.stream().skip(1).limit(2).collect(Collectors.toList()));
        System.out.println(collection.stream().reduce((s1, s2) -> s1 + s2).orElse("Empty"));

        Map<String, Book> map = new LinkedHashMap<>();
        map.put("101", new Book("Effective Java", "Bloch"));
        map.put("99", new Book("Java for beginners", "Shildt"));
        map.put("100", new Book("Network", "Tannenbaum"));

        List<String> mapList = map.values().stream()
                .filter((b) -> b.title.contains("Java")).map((b) -> b.title)
                .collect(Collectors.toList());
        mapList.forEach(System.out::println);
    }
}

class Book {
    String title;
    String author;

    Book(String title, String author) {
        this.title = title;
        this.author = author;
    }
}
