package org.xm.xmnlp.hanlp.dictionary.common;

import org.xm.xmnlp.hanlp.algorithm.ArrayDistance;
import org.xm.xmnlp.hanlp.collection.trie.DoubleArrayTrie;
import org.xm.xmnlp.hanlp.corpus.synonym.Synonym;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import static org.xm.xmnlp.hanlp.utility.Predefine.logger;

/**
 * @author xuming
 */
public class CommonSynonymDictionaryEx {
    DoubleArrayTrie<Long[]> trie;

    private CommonSynonymDictionaryEx() {
    }

    public static CommonSynonymDictionaryEx create(InputStream inputStream) {
        CommonSynonymDictionaryEx dictionary = new CommonSynonymDictionaryEx();
        if (dictionary.load(inputStream)) {
            return dictionary;
        }

//        TreeSet<Float> set = new TreeSet<Float>();

        return null;
    }

    public boolean load(InputStream inputStream) {
        trie = new DoubleArrayTrie<Long[]>();
        TreeMap<String, Set<Long>> treeMap = new TreeMap<String, Set<Long>>();
        String line = null;
        try {
            BufferedReader bw = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            while ((line = bw.readLine()) != null) {
                String[] args = line.split(" ");
                List<Synonym> synonymList = Synonym.create(args);
                for (Synonym synonym : synonymList) {
                    Set<Long> idSet = treeMap.get(synonym.realWord);
                    if (idSet == null) {
                        idSet = new TreeSet<Long>();
                        treeMap.put(synonym.realWord, idSet);
                    }
                    idSet.add(synonym.id);
                }
            }
            bw.close();
            List<String> keyList = new ArrayList<String>(treeMap.size());
            for (String key : treeMap.keySet()) {
                keyList.add(key);
            }
            List<Long[]> valueList = new ArrayList<Long[]>(treeMap.size());
            for (Set<Long> idSet : treeMap.values()) {
                valueList.add(idSet.toArray(new Long[0]));
            }
            int resultCode = trie.build(keyList, valueList);
            if (resultCode != 0) {
                logger.warning("构建" + inputStream + "失败，错误码" + resultCode);
                return false;
            }
        } catch (Exception e) {
            logger.warning("读取" + inputStream + "失败，可能由行" + line + "造成" + e);
            return false;
        }
        return true;
    }

    public Long[] get(String key) {
        return trie.get(key);
    }

    /**
     * 语义距离
     *
     * @param a
     * @param b
     * @return
     */
    public long distance(String a, String b) {
        Long[] itemA = get(a);
        if (itemA == null) return Long.MAX_VALUE / 3;
        Long[] itemB = get(b);
        if (itemB == null) return Long.MAX_VALUE / 3;

        return ArrayDistance.computeAverageDistance(itemA, itemB);
    }

    /**
     * 词典中的一个条目
     */
    public static class SynonymItem extends Synonym {
        /**
         * 条目的value，是key的同义词近义词列表
         */
        public Map<String, Synonym> synonymMap;

        public SynonymItem(Synonym entry, Map<String, Synonym> synonymMap) {
            super(entry.realWord, entry.id, entry.type);
            this.synonymMap = synonymMap;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append(super.toString());
            sb.append(' ');
            sb.append(synonymMap);
            return sb.toString();
        }
    }
}
