package org.xm.xmnlp.hanlp.dictionary;

import org.xm.xmnlp.hanlp.HanLP;
import org.xm.xmnlp.hanlp.collection.trie.DoubleArrayTrie;
import org.xm.xmnlp.hanlp.collection.trie.bintrie.BinTrie;
import org.xm.xmnlp.hanlp.corpus.io.ByteArray;
import org.xm.xmnlp.hanlp.corpus.io.IOUtil;
import org.xm.xmnlp.hanlp.corpus.tag.Nature;
import org.xm.xmnlp.hanlp.dictionary.other.CharTable;
import org.xm.xmnlp.hanlp.utility.LexiconUtility;
import org.xm.xmnlp.hanlp.utility.Predefine;
import org.xm.xmnlp.hanlp.utility.TextUtility;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import static org.xm.xmnlp.hanlp.utility.Predefine.logger;

/**
 * @author xuming
 */
public class CustomDictionary {
    public static BinTrie<CoreDictionary.Attribute> trie;
    public static DoubleArrayTrie<CoreDictionary.Attribute> dat = new DoubleArrayTrie<>();
    public static final String path[] = HanLP.Config.CustomDictionaryPath;

    static {
        long start = System.currentTimeMillis();
        if (!loadCustomDictionary(path[0])) {
            logger.warning("自定义词典" + Arrays.toString(path) + "加载失败");
        } else {
            logger.info("自定义词典加载成功:" + dat.size() + "个词条，耗时" + (System.currentTimeMillis() - start) + "ms");
        }
    }

    private static boolean loadCustomDictionary(String mainPath) {
        logger.info("load custom dictionary:" + mainPath);
        if (loadDat(mainPath)) return true;
        TreeMap<String, CoreDictionary.Attribute> map = new TreeMap<>();
        LinkedHashSet<Nature> customNatureCollector = new LinkedHashSet<>();
        try {
            for (String p : path) {
                Nature defaultNature = Nature.n;
                int cut = p.indexOf(' ');
                if (cut > 0) {
                    String nature = p.substring(cut + 1);
                    p = p.substring(0, cut);
                    try {
                        defaultNature = LexiconUtility.convertStringToNature(nature, customNatureCollector);
                    } catch (Exception e) {
                        logger.severe("配置文件【" + p + "】写错了！" + e);
                        continue;
                    }
                }
                logger.info("以默认词性[" + defaultNature + "]加载自定义词典" + p + "中……");
                boolean success = load(p, defaultNature, map, customNatureCollector);
                if (!success) logger.warning("error:" + p);
            }
            if (map.size() == 0) {
                logger.warning("没有加载到任何词条");
                map.put(Predefine.TAG_OTHER, null);
            }
            logger.info("正在构建DoubleArrayTrie……");
            dat.build(map);
            logger.info("正在缓存词典为dat文件……");
            List<CoreDictionary.Attribute> attributeList = new LinkedList<>();
            for (Map.Entry<String, CoreDictionary.Attribute> entry : map.entrySet()) {
                attributeList.add(entry.getValue());
            }
            DataOutputStream out = new DataOutputStream(IOUtil.newOutputStream(mainPath + Predefine.BIN_EXT));
            IOUtil.writeCustomNature(out, customNatureCollector);
            out.writeInt(attributeList.size());
            for (CoreDictionary.Attribute attribute : attributeList) {
                attribute.save(out);
            }
            dat.save(out);
            out.close();

        } catch (FileNotFoundException e) {
            logger.severe("自定义词典" + mainPath + "不存在！" + e);
            return false;
        } catch (IOException e) {
            logger.severe("自定义词典" + mainPath + "读取错误！" + e);
            return false;
        } catch (Exception e) {
            logger.warning("自定义词典" + mainPath + "缓存失败！\n" + TextUtility.exceptionToString(e));
        }
        return true;
    }

    public static boolean load(String path, Nature defaultNature, TreeMap<String, CoreDictionary.Attribute> map, LinkedHashSet<Nature> customNatureCollector) {
        try {
            BufferedReader br = IOUtil.newBufferedReader(path);
            String line;
            while ((line = br.readLine()) != null) {
                String[] param = line.split("\\s");
                if (param[0].length() == 0) continue;
                if (HanLP.Config.Normalization) {
                    param[0] = CharTable.convert(param[0]);
                }
                int natureCount = (param.length - 1) / 2;
                CoreDictionary.Attribute attribute;
                if (natureCount == 0) {
                    attribute = new CoreDictionary.Attribute(defaultNature);
                } else {
                    attribute = new CoreDictionary.Attribute(natureCount);
                    for (int i = 0; i < natureCount; ++i) {
                        attribute.nature[i] = LexiconUtility.convertStringToNature(param[1 + 2 * i], customNatureCollector);
                        attribute.frequency[i] = Integer.parseInt(param[2 + 2 * i]);
                        attribute.totalFrequency += attribute.frequency[i];
                    }
                }
                map.put(param[0], attribute);
            }
            br.close();
        } catch (Exception e) {
            logger.severe("自定义词典" + path + "读取错误！" + e);
            return false;
        }

        return true;
    }

    public static boolean contains(String key) {
        if (dat.exactMatchSearch(key) >= 0) return true;
        return trie != null && trie.containsKey(key);
    }

    public static boolean add(String word, String natureWithFrequency) {
        if (contains(word)) return false;
        return insert(word, natureWithFrequency);
    }

    public static boolean add(String word) {
        if (HanLP.Config.Normalization) {
            word = CharTable.convert(word);
        }
        if (contains(word)) return false;
        return insert(word, null);
    }

    public static boolean insert(String word, String natureWithFrequency) {
        if (word == null) return false;
        if (HanLP.Config.Normalization) word = CharTable.convert(word);
        CoreDictionary.Attribute att = natureWithFrequency == null ? new CoreDictionary.Attribute(Nature.nz, 1) : CoreDictionary.Attribute.create(natureWithFrequency);
        if (att == null) return false;
        if (dat.set(word, att)) return true;
        if (trie == null) trie = new BinTrie<>();
        trie.put(word, att);
        return true;
    }

    public static boolean insert(String word) {
        return insert(word, null);
    }

    static boolean loadDat(String path) {
        try {
            ByteArray byteArray = ByteArray.createByteArray(path + Predefine.BIN_EXT);
            if (byteArray == null) return false;
            int size = byteArray.nextInt();
            if (size < 0) {
                while (++size <= 0) {
                    Nature.create(byteArray.nextString());
                }
                size = byteArray.nextInt();
            }
            CoreDictionary.Attribute[] attributes = new CoreDictionary.Attribute[size];
            final Nature[] natureIndexArray = Nature.values();
            for (int i = 0; i < size; ++i) {
                int currentTotalFrequency = byteArray.nextInt();
                int len = byteArray.nextInt();
                attributes[i] = new CoreDictionary.Attribute(len);
                attributes[i].totalFrequency = currentTotalFrequency;
                for (int j = 0; j < len; ++j) {
                    attributes[i].nature[j] = natureIndexArray[byteArray.nextInt()];
                    attributes[i].frequency[j] = byteArray.nextInt();
                }
            }
            if (!dat.load(byteArray, attributes)) return false;
        } catch (Exception e) {
            logger.warning("读取失败，问题发生在" + TextUtility.exceptionToString(e));
            return false;
        }
        return true;
    }

    public static CoreDictionary.Attribute get(String key) {
        if (HanLP.Config.Normalization) key = CharTable.convert(key);
        CoreDictionary.Attribute attribute = dat.get(key);
        if (attribute != null) return attribute;
        if (trie == null) return null;
        return trie.get(key);
    }

    public static void remove(String key) {
        if (HanLP.Config.Normalization) key = CharTable.convert(key);
        if (trie == null) return;
        trie.remove(key);
    }

    /**
     * 前缀查询
     *
     * @param key
     * @return
     */
    public static LinkedList<Map.Entry<String, CoreDictionary.Attribute>> commonPrefixSearch(String key) {
        return trie.commonPrefixSearchWithValue(key);
    }

    public static BaseSearcher getSearcher(String text) {
        return new Searcher(text);
    }

    @Override
    public String toString() {
        return "CustomDictionary{" +
                "trie=" + trie +
                '}';
    }

    static class Searcher extends BaseSearcher<CoreDictionary.Attribute> {
        int begin;
        private LinkedList<Map.Entry<String, CoreDictionary.Attribute>> entryList;

        protected Searcher(char[] c) {
            super(c);
            entryList = new LinkedList<>();
        }

        protected Searcher(String text) {
            super(text);
            entryList = new LinkedList<Map.Entry<String, CoreDictionary.Attribute>>();
        }

        @Override
        public Map.Entry<String, CoreDictionary.Attribute> next() {
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
            Map.Entry<String, CoreDictionary.Attribute> result = entryList.getFirst();
            entryList.removeFirst();
            offset = begin - 1;
            return result;
        }
    }

    public static BinTrie<CoreDictionary.Attribute> getTrie() {
        return trie;
    }

    /**
     * 解析一段文本（目前采用了BinTrie+DAT的混合储存形式，此方法可以统一两个数据结构）
     *
     * @param text      文本
     * @param processor 处理器
     */
//    public static void parseText(char[] text, AhoCorasickDoubleArrayTrie.IHit<CoreDictionary.Attribute> processor) {
//        if (trie != null) {
//            BaseSearcher searcher = CustomDictionary.getSearcher(text);
//            int offset;
//            Map.Entry<String, CoreDictionary.Attribute> entry;
//            while ((entry = searcher.next()) != null) {
//                offset = searcher.getOffset();
//                processor.hit(offset, offset + entry.getKey().length(), entry.getValue());
//            }
//        }
//        DoubleArrayTrie<CoreDictionary.Attribute>.Searcher searcher = dat.getSearcher(text, 0);
//        while (searcher.next()) {
//            processor.hit(searcher.begin, searcher.begin + searcher.length, searcher.value);
//        }
//    }
}
