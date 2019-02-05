package com.talgat;

public class QuickSort {

    public static void main(String[] args) {
        int[] arr = new int[] {2, 1, 0, 6, 3, -1, -2};

        sort(arr);
        for (int a : arr) {
            System.out.print(a + " ");
        }
    }

    public static void sort(int[] array) {
        int start = 0;
        int end = array.length - 1;
        doSort(array, start, end);
    }

    private static void doSort(int[] array, int start, int end) {
        if (start >= end) return;

        int i = start, j = end;
        int c = i - (i - j) / 2;
        System.out.println("cur " + c);

        while (i < j) {
            while (i < c && array[i] <= array[c]) {
                i++;
            }
            while (j > c && array[c] <= array[j]) {
                j--;
            }
            System.out.println("i " + i + " j " + j);
            if (i < j) {
                int temp = array[i];
                array[i] = array[j];
                array[j] = temp;
                if (i == c) c = j;
                else if (j == c) c = i;
            }
        }
        System.out.println(" ii " + i + " jj " + j);
        doSort(array, start, c);
        doSort(array, c + 1, end);
    }
}
