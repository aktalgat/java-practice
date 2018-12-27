package com.talgat;


public class MyThread {

    public static void main(String[] args) {
        NewThread nt1 = new NewThread("Один");
        NewThread nt2 = new NewThread("Два");
        NewThread nt3 = new NewThread("Три");

        System.out.println("Поток Один запущен: " + nt1.thread.isAlive());
        System.out.println("Поток Два запущен: " + nt2.thread.isAlive());
        System.out.println("Поток Три запущен: " + nt3.thread.isAlive());

        try {
            System.out.println("Ожидание завершения потоков...");
            nt1.thread.join();
            //nt2.thread.join();
            //nt3.thread.join();
        } catch (InterruptedException e) {
            System.out.println("Главный поток прерван");
        }
        System.out.println("Поток Один запущен: " + nt1.thread.isAlive());
        System.out.println("Поток Два запущен: " + nt2.thread.isAlive());
        System.out.println("Поток Три запущен: " + nt3.thread.isAlive());
        System.out.println("Главный поток завершен");
    }
}

class NewThread implements Runnable {
    String name;
    Thread thread;

    NewThread(String name) {
        this.name = name;
        this.thread = new Thread(this, name);
        System.out.println("Новый поток: " + thread);
        thread.start();
    }

    @Override
    public void run() {
        try {
            for (int i = 5; i > 0; i--) {
                System.out.println(name + ": " + i);
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            System.out.println(name + " прерван");
        }
        System.out.println(name + " завершен");
    }
}
