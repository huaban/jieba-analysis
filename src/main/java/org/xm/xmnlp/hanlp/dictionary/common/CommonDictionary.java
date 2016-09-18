package org.xm.xmnlp.hanlp.dictionary.common;

import org.xm.xmnlp.hanlp.collection.trie.DoubleArrayTrie;
import org.xm.xmnlp.hanlp.corpus.io.IOUtil;
import org.xm.xmnlp.hanlp.dictionary.BaseSearcher;

import java.io.BufferedReader;
import java.util.*;

import static org.xm.xmnlp.hanlp.utility.Predefine.logger;

/**
 * @author xuming
 */
public abstract class CommonDictionary<V> {
    DoubleArrayTrie<V> trie;

    public boolean load(String path) {
        trie = new DoubleArrayTrie<V>();
        long start = System.currentTimeMillis();
        V[] valueArray = onLoadValue(path);
        if (valueArray == null) {
            logger.info("加载值" + path + ".value.dat失败，耗时" + (System.currentTimeMillis() - start) + "ms");
            return false;
        }
        logger.info("加载值" + path + ".value.dat成功，耗时" + (System.currentTimeMillis() - start) + "ms");
        long start1 = System.currentTimeMillis();
        if (loadDat(path + ".trie.dat", valueArray)) {
            logger.info("加载键" + path + ".trie.dat成功，耗时" + (System.currentTimeMillis() - start) + "ms");
            return true;
        }
        List<String> keyList = new ArrayList<>(valueArray.length);
        try {
            BufferedReader br = IOUtil.newBufferedReader(path);
            String line;
            while ((line = br.readLine()) != null) {
                String[] paramArray = line.split("\\s");
                keyList.add(paramArray[0]);
            }
            br.close();
        } catch (Exception e) {
            logger.warning("读取" + path + "失败" + e);
        }
        int resultCode = trie.build(keyList, valueArray);
        if (resultCode != 0) {
            logger.warning("trie建立失败" + resultCode + ",正在尝试排序后重载");
            TreeMap<String, V> map = new TreeMap<String, V>();
            for (int i = 0; i < valueArray.length; ++i) {
                map.put(keyList.get(i), valueArray[i]);
            }
            trie = new DoubleArrayTrie<V>();
            trie.build(map);
            int i = 0;
            for (V v : map.values()) {
                valueArray[i++] = v;
            }
        }
        trie.save(path + ".trie.dat");
        onSaveValue(valueArray, path);
        logger.info(path + "success.");
        return true;
    }

    private boolean loadDat(String path, V[] valueArray) {
        if (trie.load(path, valueArray)) return true;
        return false;
    }

    public V get(String key) {
        return trie.get(key);
    }

    public boolean contains(String key) {
        return get(key) != null;
    }

    public int size() {
        return trie.size();
    }

    protected abstract V[] onLoadValue(String path);

    protected abstract boolean onSaveValue(V[] valueArray, String path);

    public BaseSearcher getSearcher(String text) {
        return new Searcher(text);
    }

    public class Searcher extends BaseSearcher<V> {
        int begin;
        private List<Map.Entry<String, V>> entryList;

        protected Searcher(char[] c) {
            super(c);
        }

        protected Searcher(String text) {
            super(text);
            entryList = new LinkedList<>();
        }

        @Override
        public Map.Entry<String, V> next() {
            while (entryList.size() == 0 && begin < c.length) {
                entryList = trie.commonPrefixSearchWithValue(c, begin);
                ++begin;
            }
            if (entryList.size() == 0 && begin < c.length) {
                entryList = trie.commonPrefixSearchWithValue(c, begin);
                ++begin;
            }
            if (entryList.size() == 0) {
                return null;
            }
            Map.Entry<String, V> result = entryList.get(0);
            entryList.remove(0);
            offset = begin - 1;
            return result;
        }


    }
}
