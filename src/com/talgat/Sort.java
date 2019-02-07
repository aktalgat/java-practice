package com.talgat;

public class Sort {

    public static void main(String[] args) {
        int[] arr = new int[] {1, 0, 1, 1, 0, 0, 0};
        int[] newArr = new int[arr.length];

        int j = 0, k = arr.length - 1;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == 0) {
                newArr[j++] = arr[i];
            } else {
                newArr[k--] = arr[i];
            }
        }
        for (int i : newArr) {
            System.out.println(i);
        }
    }
}
