package com.github.stagirs.lingvo.morpho.tikhon;

import com.github.stagirs.common.document.Document;
import com.github.stagirs.common.document.Point;
import com.github.stagirs.common.text.TextUtils;
import com.github.stagirs.docextractor.wiki.WikiDocProcessor;
import com.github.stagirs.lingvo.morpho.MorphoAnalyst;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.junit.Test;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/*
* ищем слова из in которых нет в словаре
* записываем в out
*
* */

public class NotFoundWordsTest {


    @Test
    public void notFoundWords() throws IOException {
        System.out.println("NotFoundWords");

        File in = new File("./out/ruwiki");

        File out_nfw = new File("./out/nfw");
        File out_nfw_n = new File("./out/nfw_n");
        File out_istitle = new File("./out/nfw_istitle");
        File out_istitle_n = new File("./out/nfw_istitle_n");

        out_nfw.delete();
        out_nfw_n.delete();
        out_istitle.delete();
        out_istitle_n.delete();

        BufferedWriter bw_nfw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out_nfw), "utf-8"));
        BufferedWriter bw_nfw_n = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out_nfw_n), "utf-8"));
        BufferedWriter bw_istitle = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out_istitle), "utf-8"));
        BufferedWriter bw_istitle_n = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out_istitle_n), "utf-8"));

        WikiDocProcessor proc = new WikiDocProcessor();
        LineIterator iter = null;

        Map<String, Integer> find = new HashMap<String, Integer>();
        Set<String> isTitle = new HashSet<String>();

        long count = 0;
        long count_istitle = 0;

        try {
            iter = FileUtils.lineIterator(in, "utf-8");
            while (iter.hasNext()) {
                Document doc = proc.processDocument("", iter.nextLine());
                Set<String> findInDoc = new HashSet<String>();

                for(Point point : doc.getPoints()) {
                    for (String word : TextUtils.splitWords(point.getText(), true)) {
                        if (word.isEmpty() || word.charAt(0) < 'а' || 'я' < word.charAt(0)) {
                            continue;
                        }
                        if (MorphoAnalyst.find(word) == null) {
                            if (!findInDoc.contains(word)) {
                                findInDoc.add(word);
                            }
                            if (!isTitle.contains(word) && point.isTitle()) {
                                isTitle.add(word);
                            }

                        }
                    }
                }
                for (String word : findInDoc) {
                    if (!find.containsKey(word)) {
                        find.put(word, 1);
                    } else {
                        find.put(word, find.get(word) + 1);
                    }
                }
            }
            for (String word : find.keySet()) {
                bw_nfw.append(word + "\n");
                bw_nfw_n.append(word + "\t" + find.get(word) + "\n");
                count++;
            }
            for (String word : isTitle) {
                bw_istitle.append(word + "\n");
                bw_istitle_n.append(word + "\t" + find.get(word) + "\n");
                count_istitle++;
            }
        } catch (IOException e) {
            System.err.println(e);
        } finally {
            bw_nfw.close();
            bw_nfw_n.close();
            bw_istitle.close();
            bw_istitle_n.close();
            if (iter != null) {
                iter.close();
            }
        }

        System.out.println("в Википедии найдено " + count + " слов, которых нет в нашем словаре");
        System.out.println("среди заголовков Википедии найдено " + count_istitle + " слов, которых нет в нашем словаре");
    }



    @Test
    public void isTitleOften() throws IOException {
        System.out.println("IsTitleOften");

        File in = new File("./out/nfw_istitle_n");

        File out1 = new File("./out/nfw_istitle_nMore1_Lmore4");
        File out3 = new File("./out/nfw_istitle_nMore3_Lmore4");

        out1.delete();
        out3.delete();

        BufferedWriter bw1 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out1), "utf-8"));
        BufferedWriter bw3 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out3), "utf-8"));

        LineIterator iter = null;

        long count1 = 0;
        long count3 = 0;

        try {
            iter = FileUtils.lineIterator(in, "utf-8");
            while (iter.hasNext()) {
                String[] words = iter.nextLine().split("\t");
                int n = Integer.parseInt(words[1]);
                if (words[0].length() > 4 && n > 1) {
                    bw1.append(words[0] + "\n");
                    count1++;
                    if (n > 3) {
                        bw3.append(words[0] + "\n");
                        count3++;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println(e);
        } finally {
            bw1.close();
            bw3.close();
            if (iter != null) {
                iter.close();
            }
        }

        System.out.println("слов из заголовков длиной > 4, которые встречаются еще хотя бы в 1 статье = " + count1);
        System.out.println("слов из заголовков длиной > 4, которые встречаются еще хотя бы в 3 статье = " + count3);
    }

}
