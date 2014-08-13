package com.huaban.analysis.jieba;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


public class WordDictionary {
    private static WordDictionary singleton;
    private static final String MAIN_DICT = "/dict.txt";
    private static String USER_DICT_SUFFIX = ".dict";

    public final Map<String, Double> freqs = new HashMap<String, Double>();
    public final Set<String> loadedPath = new HashSet<String>();
    private Double minFreq = Double.MAX_VALUE;
    private Double total = 0.0;
    private DictSegment _dict;


    private WordDictionary() {
        this.loadDict();
    }


    public static WordDictionary getInstance() {
        if (singleton == null) {
            synchronized (WordDictionary.class) {
                if (singleton == null) {
                    singleton = new WordDictionary();
                    return singleton;
                }
            }
        }
        return singleton;
    }


    /**
     * for ES to initialize the user dictionary.
     * 
     * @param configFile
     */
    public void init(File configFile) {
        String path = configFile.getAbsolutePath();
        System.out.println("initialize user dictionary:" + path);
        synchronized (WordDictionary.class) {
            if (loadedPath.contains(path))
                return;
            for (File userDict : configFile.listFiles()) {
                if (userDict.getPath().endsWith(USER_DICT_SUFFIX)) {
                    singleton.loadUserDict(userDict);
                    loadedPath.add(path);
                }
            }
        }
    }


    public void loadDict() {
        _dict = new DictSegment((char) 0);
        InputStream is = this.getClass().getResourceAsStream(MAIN_DICT);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));

            long s = System.currentTimeMillis();
            while (br.ready()) {
                String line = br.readLine();
                String[] tokens = line.split("[\t ]+");

                if (tokens.length < 2)
                    continue;

                String word = tokens[0];
                double freq = Double.valueOf(tokens[1]);
                total += freq;
                word = addWord(word);
                freqs.put(word, freq);
            }
            // normalize
            for (Entry<String, Double> entry : freqs.entrySet()) {
                entry.setValue((Math.log(entry.getValue() / total)));
                minFreq = Math.min(entry.getValue(), minFreq);
            }
            System.out.println(String.format("main dict load finished, time elapsed %d ms",
                System.currentTimeMillis() - s));
        }
        catch (IOException e) {
            System.err.println(String.format("%s load failure!", MAIN_DICT));
        }
        finally {
            try {
                if (null != is)
                    is.close();
            }
            catch (IOException e) {
                System.err.println(String.format("%s close failure!", MAIN_DICT));
            }
        }
    }


    private String addWord(String word) {
        if (null != word && !"".equals(word.trim())) {
            String key = word.trim().toLowerCase();
            _dict.fillSegment(key.toCharArray());
            return key;
        }
        else
            return null;
    }


    public void loadUserDict(File userDict) {
        loadUserDict(userDict, Charset.forName("UTF-8"));
    }


    public void loadUserDict(File userDict, Charset charset) {
        InputStream is;
        try {
            is = new FileInputStream(userDict);
        }
        catch (FileNotFoundException e) {
            System.err.println(String.format("could not find %s", userDict.getAbsolutePath()));
            return;
        }
        try {
            @SuppressWarnings("resource")
            BufferedReader br = new BufferedReader(new InputStreamReader(is, charset));
            long s = System.currentTimeMillis();
            int count = 0;
            while (br.ready()) {
                String line = br.readLine();
                String[] tokens = line.split("[\t ]+");

                if (tokens.length < 2)
                    continue;

                String word = tokens[0];
                double freq = Double.valueOf(tokens[1]);
                word = addWord(word);
                freqs.put(word, Math.log(freq / total));
                count++;
            }
            System.out.println(String.format("user dict %s load finished, tot words:%d, time elapsed:%dms",
                userDict.getAbsolutePath(), count, System.currentTimeMillis() - s));
        }
        catch (IOException e) {
            System.err.println(String.format("%s: load user dict failure!", userDict.getAbsolutePath()));
        }
        finally {
            try {
                if (null != is)
                    is.close();
            }
            catch (IOException e) {
                System.err.println(String.format("%s close failure!", userDict.getAbsolutePath()));
            }
        }
    }


    public DictSegment getTrie() {
        return this._dict;
    }


    public boolean containsWord(String word) {
        return freqs.containsKey(word);
    }


    public Double getFreq(String key) {
        if (containsWord(key))
            return freqs.get(key);
        else
            return minFreq;
    }
}
