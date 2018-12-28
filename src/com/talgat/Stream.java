package com.talgat;

import java.util.Arrays;
import java.util.List;
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

    }
}
