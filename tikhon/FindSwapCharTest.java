package com.github.stagirs.lingvo.morpho.tikhon;

import com.github.stagirs.lingvo.morpho.MorphoAnalyst;
import com.github.stagirs.lingvo.morpho.MyStringUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.junit.Test;

import java.io.*;

public class FindSwapCharTest {

    public static File in = new File("./out/nfw");
    public static File out = new File("./myout/nfw_find1swapchar+lmore5");

    @Test
    public void findSwapChar() throws IOException {
        System.out.println("FindSwapChar");

        out.delete();

        LineIterator iter = null;
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out), "utf-8"));

        long count = 0;

        try {
            iter = FileUtils.lineIterator(in, "utf-8");

            while (iter.hasNext()) {
                String word = iter.nextLine();

                boolean find = false;
                boolean find1 = false;
                String findWord = "";
                for (String str : MyStringUtils.SwapChar(word)) {
                    if (MorphoAnalyst.find(str) != null) {
                        if (!find) {
                            find = true;
                            find1 = true;
                            findWord = str;
                        } else {
                            find1 = false;
                        }
                    }
                }

                if (find1 && word.length() > 5  ) {
                    bw.append(word + "\t" + findWord + "\n");
                    count++;
                }

            }
        } catch (IOException e) {
            System.err.println(e);
        } finally {
            bw.close();
            if (iter != null) {
                iter.close();
            }
        }

        System.out.println("слов, длиной больше 5, где перемешаны две соседние буквы,\nпричем только 1 слово в словаре нашлось -\n" + count);
    }

}
