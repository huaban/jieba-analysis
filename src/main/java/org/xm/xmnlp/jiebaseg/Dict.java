package org.xm.xmnlp.jiebaseg;

import com.huaban.analysis.jieba.WordDictionary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by mingzai on 2016/9/10.
 */
public class Dict {

    private static Dict dict;
    private static final String MAIN_DICT = "/dict.txt";
    public static final String SOUGOU_DICT = "/sougou.dict";
    private static String USER_DICT_SUFFIX = ".dict";

    public final Map<String, Double> freqs = new HashMap<String, Double>();
    public final Map<String, String> natures = new HashMap<>();
    public final Set<String> loadedPath = new HashSet<>();

    private Double minFreq = Double.MIN_VALUE;
    private Double total = 0.0;
    private Branch branch;

    private Dict() {
        branch = new Branch((char) 0);
        this.loadDict();
//        this.loadSougouDict();
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
        InputStream is = this.getClass().getResourceAsStream(MAIN_DICT);
        BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
        try {
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
                if (null != br) br.close();
                if (null != is) is.close();
            } catch (IOException e) {
                System.err.println(String.format(Locale.getDefault(), "%s close failure !", MAIN_DICT));
            }
        }
    }

    public void loadUserDict(Path userDict) {
        loadUserDict(userDict, StandardCharsets.UTF_8);
    }

    public void loadUserDict(Path userDict, Charset charset) {
        try {
            BufferedReader br = Files.newBufferedReader(userDict, charset);
            long s = System.currentTimeMillis();
            int count = 0;
            while (br.ready()) {
                String line = br.readLine();
                String[] tokens = line.split("[\t ]+");
                if (tokens.length < 1) {
                    continue;
                }
                String word = tokens[0];
                double freq = 3.0;
                if (tokens.length >= 2) {
                    freq = Double.valueOf(tokens[1]);
                }
                String nature = "";
                if (tokens.length >= 3) {
                    nature = String.valueOf(tokens[2]);
                }
                word = addWord(word);
                freqs.put(word, Math.log(freq / total));
                natures.put(word, nature);
                count++;
            }

            System.out.println(String.format(Locale.getDefault(), "user dict %s load finished. total words num:%d,time spend:%d ms", userDict.toString(),
                    count, System.currentTimeMillis() - s));
            br.close();
        } catch (IOException e) {
            System.err.println(String.format(Locale.getDefault(), "%s: load user dict failure!", userDict.toString()));
        }
    }

    public void init(Path configFile) {
        String abspath = configFile.toAbsolutePath().toString();
        System.out.println("initialize user dictionary:" + abspath);
        synchronized (WordDictionary.class) {
            if (loadedPath.contains(abspath))
                return;

            DirectoryStream<Path> stream;
            try {
                stream = Files.newDirectoryStream(configFile, String.format(Locale.getDefault(), "*%s", USER_DICT_SUFFIX));
                for (Path path : stream) {
                    System.err.println(String.format(Locale.getDefault(), "loading dict %s", path.toString()));
                    dict.loadUserDict(path);
                }
                loadedPath.add(abspath);
            } catch (IOException e) {
                System.err.println(String.format(Locale.getDefault(), "%s: load user dict failure!", configFile.toString()));
            }
        }
    }

    public void loadSougouDict() {
        Path path = null;
        try {
            path = Paths.get(this.getClass().getResource(SOUGOU_DICT).toURI());
            dict.loadUserDict(path);
        } catch (URISyntaxException e) {
            e.printStackTrace();
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

    public String getNature(String key) {
        if (containsNature(key)) {
            return natures.get(key);
        }
        return "";
    }

    private boolean containsNature(String key) {
        return natures.containsKey(key);
    }
}
