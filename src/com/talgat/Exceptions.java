package com.talgat;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Exceptions {

    public static void main(String[] args) {
        try {
            readFirstLineFromFile(".gitignore");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String readFirstLineFromFile(String path) throws IOException {
        try  {
            BufferedReader br =
                    new BufferedReader(new FileReader(path));
            return br.readLine();
        } finally {
            System.out.println("H ha");
        }
    }
}
