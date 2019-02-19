package com.talgat;

public class ReferenceExample {

    public static void main(String[] args) {
        int a = 5;
        int b = 8;
        swap(a, b);
        System.out.println("a " + a + " b " + b);

        Integer a1 = 5;
        Integer b1 = 8;
        swapRef(a1, b1);
        System.out.println("a1 " + a1 + " b1 " + b1);

        StringBuilder stringBuilder = new StringBuilder("12");
        append(stringBuilder);
        System.out.println("String builder " + stringBuilder.toString());
    }

    private static void swap(int a, int b) {
        int tmp = a;
        a = b;
        b = tmp;
    }

    private static void swapRef(Integer a, Integer b) {
        Integer tmp = a;
        a = b;
        b = tmp;

        System.out.println("Inner a " + a + " b " + b);
    }

    private static void append(StringBuilder stringBuilder) {
        stringBuilder.append("23");
    }
}
