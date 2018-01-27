package com.huaban.analysis.jieba;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


@Slf4j
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
    public void init(Path configFile) {
        String abspath = configFile.toAbsolutePath().toString();
        log.info("initialize user dictionary: {}", abspath);
        synchronized (WordDictionary.class) {
            if (loadedPath.contains(abspath))
                return;
            
            DirectoryStream<Path> stream;
            try {
                stream = Files.newDirectoryStream(configFile, String.format(Locale.getDefault(), "*%s", USER_DICT_SUFFIX));
                for (Path path: stream){
                    log.info("loading dict {}", path);
                    singleton.loadUserDict(path);
                }
                loadedPath.add(abspath);
            } catch (IOException e) {
                log.error("{}: load user dict failure", configFile, e);
            }
        }
    }
    
    
    /**
     * let user just use their own dict instead of the default dict
     */
    public void resetDict(){
    	_dict = new DictSegment((char) 0);
    	freqs.clear();
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
            log.info("main dict load finished, time elapsed {} ms", System.currentTimeMillis() - s);
        } catch (IOException e) {
            log.error("{} load failure", MAIN_DICT, e);
        } finally {
            try {
                if (null != is)
                    is.close();
            } catch (IOException e) {
                log.error("{} close failure!", MAIN_DICT, e);
            }
        }
    }


    private String addWord(String word) {
        if (null != word && !"".equals(word.trim())) {
            String key = word.trim().toLowerCase(Locale.getDefault());
            _dict.fillSegment(key.toCharArray());
            return key;
        }
        else
            return null;
    }


    public void loadUserDict(Path userDict) {
        loadUserDict(userDict, StandardCharsets.UTF_8);
    }

    public void loadUserDict(Path userDict, Charset charset) {
        BufferedReader br = null;
        try {
            log.info("to read user dict {}", userDict);
            br = Files.newBufferedReader(userDict, charset);
            loadUserDict(br);
        } catch (IOException e) {
            log.error("load user dict {} failure!", userDict, e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    log.error("close BufferedReader failure!", e);
                }
            }
        }
    }

    public void loadUserDict(InputStream is) {
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        try {
            log.info("to read user dict from InputStream");
            loadUserDict(br);
        } catch (IOException e) {
            log.error("load user dict failure!", e);
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                log.error("close BufferedReader failure!", e);
            }
        }
    }

    public void loadUserDict(BufferedReader br) throws IOException {
          long s = System.currentTimeMillis();
          int count = 0;
          while (br.ready()) {
              String line = br.readLine();
              String[] tokens = line.split("[\t ]+");

              if (tokens.length < 1) {
                  // Ignore empty line
                  continue;
              }

              String word = tokens[0];

              double freq = 3.0d;
              if (tokens.length == 2)
                  freq = Double.valueOf(tokens[1]);
              word = addWord(word);
              freqs.put(word, Math.log(freq / total));
              count++;
          }
          log.info("user dict load finished, total words: {}, time elapsed: {} ms", count, System.currentTimeMillis() - s);
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
