package com.talgat;

public class Main {

    public static void main(String[] args) {
        MainMax<Integer> mainMax = new MainMax<>(2000);
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 3000; i++) {
                mainMax.push(i);
                System.out.println(i);
            }
        });

        Thread t2 = new Thread(() -> {
            try {
                System.out.println(mainMax.top());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        t1.start();
        t2.start();
    }
}
