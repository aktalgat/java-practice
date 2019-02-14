package com.talgat;

import java.util.Optional;

public class OpionalExample {

    public static void main(String[] args) {
        Optional<Integer> optional = Optional.of(90);
        optional.get();
    }
}
