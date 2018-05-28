package com.github.stagirs.lingvo.morpho;

import java.util.LinkedList;
import java.util.List;

public class MyStringUtils {

    public static List<String> LevDist1 (String word) {
        List<String> list = new LinkedList<String>();

        for (int i = 0; i < word.length(); i++) {
            list.add(word.substring(0,i) + word.substring(i+1, word.length()));
            for (char ch = 'а'; ch <= 'я'; ch++) {
                list.add(word.substring(0, i) + ch + word.substring(i+1, word.length()));
                list.add(word.substring(0, i) + ch + word.substring(i, word.length()));
            }

        }
        for (char ch = 'а'; ch <= 'я'; ch++) {
            list.add(word + ch);
        }
        return list;
    }

    public static List<String> SwapChar (String word) {
        List<String> list = new LinkedList<String>();

        for (int i = 0; i < word.length() - 1; i++) {
            list.add(word.substring(0, i) + word.charAt(i+1) + word.charAt(i) + word.substring(i+2));
        }


        return  list;
    }

}
