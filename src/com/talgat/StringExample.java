package com.talgat;

public class StringExample {

    public static void main(String[] args) {
        String s1 = "ab";
        String s2 = "ab";

        if (s1 == s2) {
            System.out.println("They are equals to due to String pool");
        }
    }
}
