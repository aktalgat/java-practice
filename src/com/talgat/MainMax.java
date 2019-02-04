package com.talgat;

import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

public class MainMax<T extends Comparable<T>> {
    private int n;
    private TreeSet<T> set = new TreeSet<T>(Comparator.reverseOrder());

    MainMax(int n) {
        this.n = n;
    }

    public synchronized void push(T val) {
        set.add(val);
        if (set.size() > n) {
            set.remove(set.last());
        }
        if (set.size() >= 1000) {
            notifyAll();
        }
    }

    public synchronized Set<T> top() throws InterruptedException {
        if (set.size() < 1000) {
            wait();
        }
        return new TreeSet<>(set);
    }
}