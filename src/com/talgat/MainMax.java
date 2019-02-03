package com.talgat;

import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

public class MainMax<T extends Comparable<T>> {
    private int N;
    private TreeSet<T> set = new TreeSet<T>(Comparator.reverseOrder());

    MainMax(int N) {
        this.N = N;
    }

    public synchronized void push(T val) {
        set.add(val);
        if (set.size() > N) {
            set.remove(set.last());
        }
    }

    public synchronized Set<T> top() {
        return Collections.unmodifiableSet(set);
    }
}