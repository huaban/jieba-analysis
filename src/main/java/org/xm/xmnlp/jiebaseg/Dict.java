package org.xm.xmnlp.jiebaseg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by mingzai on 2016/9/10.
 */
public class Dict {

    private static Dict dict;
    private static final String MAIN_DICT = "/dict.txt";
    private static String USER_DICT_SUFFIX = ".dict";

    public final Map<String, Double> freqs = new HashMap<String, Double>();
    public final Map<String, String> natures = new HashMap<>();
    public final Set<String> loadPath = new HashSet<>();

    private Double minFreq = Double.MIN_VALUE;
    private Double total = 0.0;
    private Branch branch;
    private String nature;

    private Dict() {
        this.loadDict();
    }

    public static Dict getInstance() {
        if (dict == null) {
            synchronized (Dict.class) {
                if (dict == null) {
                    dict = new Dict();
                    return dict;
                }
            }
        }
        return dict;
    }

    private void loadDict() {
        branch = new Branch((char) 0);
        InputStream is = this.getClass().getResourceAsStream(MAIN_DICT);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            long start = System.currentTimeMillis();
            while (br.ready()) {
                String line = br.readLine();
                String[] tokens = line.split("[\t ]+");
                if (tokens.length < 2) {
                    continue;
                }
                String word = tokens[0];
                double freq = 0.0;
                if (tokens[1] != null) {
                    freq = Double.valueOf(tokens[1]);
                }
                String nature = "";
                if (tokens[2] != null) {
                    nature = String.valueOf(tokens[2]);
                }
                total += freq;
                word = addWord(word);
                freqs.put(word, freq);
                natures.put(word, nature);
            }
            for (Map.Entry<String, Double> entry : freqs.entrySet()) {
                entry.setValue((Math.log(entry.getValue() / total)));
                minFreq = Math.min(entry.getValue(), minFreq);
            }
            System.out.println(String.format(Locale.getDefault(), "main dict load finished,spend %d ms ",
                    System.currentTimeMillis() - start));
        } catch (IOException e) {
            System.err.println("main dict load failure!" + MAIN_DICT);
        } finally {
            try {
                if (null != is)
                    is.close();
            } catch (IOException e) {
                System.err.println(String.format(Locale.getDefault(), "%s close failure !", MAIN_DICT));
            }
        }
    }

    private String addWord(String word) {
        if (null != word && !word.trim().equals("")) {
            String key = word.trim().toLowerCase(Locale.getDefault());
            branch.fillBranch(key.toCharArray());
            return key;
        }

        return null;
    }


    public Branch getBranch() {
        return this.branch;
    }

    public boolean containsWord(String word) {
        return freqs.containsKey(word);
    }

    public Double getFreq(String key) {
        if (containsWord(key)) {
            return freqs.get(key);
        }
        return minFreq;

    }

    public  String getNature(String key) {
        if (containsNature(key)) {
            return natures.get(key);
        }
        return "";
    }

    private boolean containsNature(String key) {
        return natures.containsKey(key);
    }
}
