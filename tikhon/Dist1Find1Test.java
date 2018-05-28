package com.github.stagirs.lingvo.morpho.tikhon;

import com.github.stagirs.lingvo.morpho.MorphoAnalyst;
import com.github.stagirs.lingvo.morpho.MyStringUtils;
import com.github.stagirs.lingvo.morpho.model.Morpho;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.junit.Test;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

/*
Dist1Find1
    среди слов, из файла in_nfw записываем в "./myout/nfw_dist1+find1"
    только слова, для которых существует слово из словаря на расстоянии Левинштейна = 1
    запись производится построчно, <само слово> + "\t" + <единственное слово, из словаря, до которого расстояние = 1>
NoTitle
    из списка слов с их исправлениями, полученного выше
    оставляем только слова и исправления,
    которых нет в заголовках (файл in_istitle)
LessEqualMore
    преобразуем список слов и их исправлений
    по типу исправления,
    добавляя после пары слов одну букву: l, e или m:
        если в слове не хватало одной буквы (less)         -> l
        если в слове была одна неправильная буква (equal)  -> e
        если в слове была одна лишняя буква (more)         -> m
 */
public class Dist1Find1Test {

    //public static File in_nfw = new File("./out_old/nfw");
    public static File in_istitle = new File("./out/nfw_istitle");


    @Test
    public void dist1Find1() throws IOException {
        System.out.println("Dist1Find1");

        File in = new File("./out/nfw");
        File out = new File("./out/nfw_dist1+find1");
        long count = 0;

        out.delete();

        LineIterator iter = null;
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out), "utf-8"));

        try {
            iter = FileUtils.lineIterator(in, "utf-8");
            while (iter.hasNext()) {
                String word = iter.nextLine();

                boolean dist1 = false;
                boolean find1 = false;
                String findWord = "";
                for (String str : MyStringUtils.LevDist1(word)) {
                    Morpho morpho = MorphoAnalyst.find(str);
                    if (morpho != null) {
                        if (!dist1) {
                            dist1 = true;
                            find1 = true;
                            findWord = morpho.getWord();
                        } else {
                            find1 = false;
                        }
                    }
                }
                if (find1 && findWord.length() > 4) {
                    count++;
                    bw.append(word + "\t" + findWord + "\n");
                    System.out.println(count);
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
        System.out.println(count + " слов, которых нет в словаре с мин. расстоянием = 1\nи всего одним таким словом на расстоянии1");
    }

    @Test
    public void noTitle() throws IOException {
        System.out.println("NoTitle");

        File in = new File("./out/nfw_dist1+find1");
        File out = new File("./out/nfw_dist1+find1+nottitle");
        long count = 0;

        out.delete();

        LineIterator iter = null;
        LineIterator iter_istitle = null;
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out), "utf-8"));
        Set<String> set = new HashSet<String>();

        try {
            iter = FileUtils.lineIterator(in, "utf-8");
            iter_istitle = FileUtils.lineIterator(in_istitle, "utf-8");

            while (iter_istitle.hasNext()) {
                set.add(iter_istitle.nextLine());
            }

            while (iter.hasNext()) {
                String[] words = iter.nextLine().split("\t");

                if (!set.contains(words[0])) {
                    bw.append(words[0] + "\t" + words[1] + "\n");
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

        System.out.println("найдено " + count + " слов, которых нет в словаре с мин. расстоянием = 1\nи всего одним таким словом на расстоянии1 + nottitle");
    }

    @Test
    public void lessEqualMore() throws IOException {
        System.out.println("LessEqualMore");

        File in = new File("./out/nfw_dist1+find1+nottitle");
        File out = new File("./out/nfw_dist1+find1+nottitle+lem");

        out.delete();

        LineIterator iter = null;
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out), "utf-8"));

        long count_e = 0;
        long count_l = 0;
        long count_m = 0;

        try {
            iter = FileUtils.lineIterator(in, "utf-8");

            /*
             * если в слове не хватало одной буквы (less)           -> l
             * если в слове была одна неправильная буква (equal)    -> e
             * если в слове была одна лишняя буква (more)           -> m
             */
            while (iter.hasNext()) {
                String line = iter.nextLine();
                String[] words = line.split("\t");

                if (words[0].length() < words[1].length()) {
                    bw.append(line + "\t" + "l\n");
                    count_l++;
                } else if (words[0].length() == words[1].length()) {
                    bw.append(line + "\t" + "e\n");
                    count_e++;
                } else {
                    bw.append(line + "\t" + "m\n");
                    count_m++;
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

        System.out.println("слов, где не хватало буквы - " + count_l);
        System.out.println("слов, где одна неправилная буква - " + count_e);
        System.out.println("слов, где одна лишняя буква - " + count_m);
    }
}
