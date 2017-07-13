package com.huaban.analysis.jieba;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;


public class WordDictionary {
    private static WordDictionary singleton;


    public static final String MAIN_DICT = "/dict.txt";


    public static final String USER_DICT_SUFFIX = ".dict";

    public final Map<String, Double> freqs = new HashMap<String, Double>();
    public final Set<String> loadedPath = new HashSet<String>();
    private Double minFreq = Double.MAX_VALUE;
    private Double total = 0.0;
    private DictSegment _dict;



    private static Logger LOG = LoggerFactory.getLogger(WordDictionary.class);


    /**
     * 自动初始化
     */
    private WordDictionary() {
        this(true);
    }


    /**
     * 选择性初始化
     * @param loadDict 是否加载词典
     */
    private WordDictionary(boolean loadDict) {
        if(loadDict) {
            this.loadDict();
            String userDictPath = System.getenv().get("USER_DICT_PATH");
            if (StringUtils.isBlank(userDictPath)) {
                userDictPath = System.getProperty("user.dict.path");
            }

            if (StringUtils.isNotBlank(userDictPath)) {
                this.addUserDictDir(Paths.get(userDictPath));
                LOG.info("add user dict path {}", userDictPath);
            }
        }
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
     * 新的实例，词典
     *
     * @param dict 词典
     * @return 返回词典
     */
    public static WordDictionary newInstance(String... dict) {
        return newInstance(Arrays.asList(dict));
    }


    /**
     * 新的实例，词典
     *
     * @param dict 词典
     * @return 返回词典
     */
    public static WordDictionary newInstance(List<String> dict) {
        WordDictionary dictionary = new WordDictionary(false);
        try {
            dictionary.resetDict();
            dictionary.loadUserDict(new StringReader(StringUtils.join(dict, "\n")));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return dictionary;
    }


    /**
     * for ES to initialize the user dictionary.
     * 
     * @param dictDir
     */
    public void addUserDictDir(Path dictDir) {
        String abspath = dictDir.toAbsolutePath().toString();
        LOG.info("initialize user dictionary:" + abspath);
        synchronized (WordDictionary.class) {
            if (loadedPath.contains(abspath))
                return;
            
            DirectoryStream<Path> stream;
            try {
                stream = Files.newDirectoryStream(dictDir, String.format(Locale.getDefault(), "*%s", USER_DICT_SUFFIX));
                for (Path path : stream){
                    LOG.info(String.format(Locale.getDefault(), "loading dict %s", path.toString()));
                    loadUserDict(path);
                }
                loadedPath.add(abspath);
            } catch (IOException e) {
                LOG.warn(String.format(Locale.getDefault(), "%s: load user dict failure!", dictDir.toString()));
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
            String line = null;
            while ((line = br.readLine()) != null) {
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
            LOG.info(String.format(Locale.getDefault(), "main dict load finished, time elapsed %d ms",
                System.currentTimeMillis() - s));
        }
        catch (IOException e) {
            LOG.warn(String.format(Locale.getDefault(), "%s load failure!", MAIN_DICT));
        }
        finally {
            try {
                if (null != is)
                    is.close();
            }
            catch (IOException e) {
                LOG.warn(String.format(Locale.getDefault(), "%s close failure!", MAIN_DICT));
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



    public void loadUserDict(Path userDict, Charset charset) {
        try {
            BufferedReader br = Files.newBufferedReader(userDict, charset);
            loadUserDict(br);
            LOG.info(String.format(Locale.getDefault(), "user dict %s load finished", userDict.toString()));
        } catch(IOException e) {
            LOG.warn(String.format(Locale.getDefault(), "%s: load user dict failure!", userDict.toString()));
        }
    }


    public void loadUserDict(Path userDict) {
        try {
            BufferedReader br = Files.newBufferedReader(userDict, StandardCharsets.UTF_8);
            loadUserDict(br);
            LOG.info(String.format(Locale.getDefault(), "user dict %s load finished", userDict.toString()));
        } catch(IOException e) {
             LOG.warn(String.format(Locale.getDefault(), "%s: load user dict failure!", userDict.toString()));
        }
    }


    public void loadUserDict(Reader reader) throws IOException {
        BufferedReader br = null;
        try {
            if(reader instanceof BufferedReader) {
                br = (BufferedReader)reader;
            } else {
                br = new BufferedReader(reader);
            }       
            long s = System.currentTimeMillis();
            int count = 0;
            String line = null;
            while ((line = br.readLine()) != null) {
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
            LOG.info(String.format(Locale.getDefault(), "load finished, tot words:%d, time elapsed:%dms",  count, System.currentTimeMillis() - s));
        } catch (IOException e) {
            throw e;        
        } finally {
            try {
                if(reader != null) {
                    reader.close();
                }
            } catch(IOException e) {

            }
        }
    }


    public DictSegment getTrie() {
        return this._dict;
    }


    public boolean containsWord(String word) {
        return freqs.containsKey(word);
    }


    /**
     * 获取频率
     * @param key
     * @return
     */
    public Double getFreq(String key) {
        Double aDouble = freqs.get(key);
        return aDouble != null ? aDouble : minFreq;
    }
}
