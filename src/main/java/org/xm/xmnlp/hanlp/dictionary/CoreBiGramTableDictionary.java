package org.xm.xmnlp.hanlp.dictionary;

import org.xm.xmnlp.hanlp.HanLP;
import org.xm.xmnlp.hanlp.corpus.io.IOUtil;
import org.xm.xmnlp.hanlp.utility.Predefine;

import java.io.*;
import java.util.Map;
import java.util.TreeMap;

import static org.xm.xmnlp.hanlp.utility.Predefine.logger;

/**
 * @author xuming
 */
public class CoreBiGramTableDictionary {
    private static int start[];
    private static int pair[];
    public static final String path = HanLP.Config.BiGramDictionaryPath;
    public static final String datPath = HanLP.Config.BiGramDictionaryPath + ".table" + Predefine.BIN_EXT;

    static {
        logger.info("开始加载二元词典" + path + ".table");
        long start = System.currentTimeMillis();
        if (!load(path)) {
            logger.severe("二元词典加载失败");
            System.exit(-1);
        } else {
            logger.info(path + ".table" + "加载成功，耗时" + (System.currentTimeMillis() - start) + "ms");
        }
    }

    private static boolean load(String path) {
        if (loadDat(datPath)) return true;
        BufferedReader br;
        TreeMap<Integer, TreeMap<Integer, Integer>> map = new TreeMap<>();
        try {
            br = IOUtil.newBufferedReader(path);
            String line;
            int total = 0;
            int maxWordId = CoreDictionary.trie.size();
            while ((line = br.readLine()) != null) {
                String[] params = line.split("\\s");
                String[] twoWord = params[0].split("@", 2);
                String a = twoWord[0];
                int idA = CoreDictionary.trie.exactMatchSearch(a);
                if (idA == -1) {
                    continue;
                }
                String b = twoWord[1];
                int idB = CoreDictionary.trie.exactMatchSearch(b);
                if (idB == -1) {
                    continue;
                }
                int freq = Integer.parseInt(params[1]);
                TreeMap<Integer, Integer> biMap = map.get(idA);
                if (biMap == null) {
                    biMap = new TreeMap<>();
                    map.put(idA, biMap);
                }
                biMap.put(idB, freq);
                total += 2;
            }
            br.close();
            start = new int[maxWordId + 1];
            pair = new int[total];
            int offset = 0;
            for (int i = 0; i < maxWordId; ++i) {
                TreeMap<Integer, Integer> bMap = map.get(i);
                if (bMap != null) {
                    for (Map.Entry<Integer, Integer> entry : bMap.entrySet()) {
                        int index = offset << 1;
                        pair[index] = entry.getKey();
                        pair[index + 1] = entry.getValue();
                        ++offset;
                    }
                }
                start[i + 1] = offset;
            }
            logger.info("二元词典读取完毕:" + path + "，构建为TableBin结构");
        } catch (FileNotFoundException e) {
            logger.severe("二元词典" + path + "不存在！" + e);
            return false;
        } catch (IOException e) {
            logger.severe("二元词典" + path + "读取错误！" + e);
            return false;
        }
        logger.info("开始缓存二元词典到" + datPath);
        if (!saveDat(datPath)) {
            logger.warning("缓存二元词典到" + datPath + "失败");
        }
        return true;
    }

    public static boolean saveDat(String path) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(IOUtil.newOutputStream(path));
            out.writeObject(start);
            out.writeObject(pair);
            out.close();
        } catch (Exception e) {
            logger.warning("在缓存" + path + "时发生异常" + e);
            return false;
        }
        return true;
    }

    public static boolean loadDat(String path) {
        try {
            ObjectInputStream in = new ObjectInputStream(IOUtil.newInputStream(path));
            start = (int[]) in.readObject();
            if (CoreDictionary.trie.size() != start.length - 1) {
                in.close();
                return false;
            }
            pair = (int[]) in.readObject();
            in.close();
        } catch (Exception e) {
            logger.warning("尝试载入缓存文件" + path + "发生异常[" + e + "]，下面将载入源文件并自动缓存……");
            return false;
        }
        return true;
    }

    /**
     * 二分搜索，由于二元接续前一个词固定时，后一个词比较少，所以二分也能取得很高的性能
     *
     * @param a         目标数组
     * @param fromIndex 开始下标
     * @param length    长度
     * @param key       词的id
     * @return 共现频次
     */
    private static int binarySearch(int[] a, int fromIndex, int length, int key) {
        int low = fromIndex;
        int high = fromIndex + length - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            int midVal = a[mid << 1];

            if (midVal < key)
                low = mid + 1;
            else if (midVal > key)
                high = mid - 1;
            else
                return mid; // key found
        }
        return -(low + 1);  // key not found.
    }

    public static int getBiFrequency(String a, String b) {
        int idA = CoreDictionary.trie.exactMatchSearch(a);
        if (idA == -1) {
            return 0;
        }
        int idB = CoreDictionary.trie.exactMatchSearch(b);
        if (idB == -1) {
            return 0;
        }
        int index = binarySearch(pair, start[idA], start[idA + 1] - start[idA], idB);
        if (index < 0) return 0;
        index <<= 1;
        return pair[index + 1];
    }

    public static int getBiFrequency(int idA, int idB) {
        if (idA == -1 || idB == -1) {
            return 1000;
        }
        int index = binarySearch(pair, start[idA], start[idA + 1] - start[idA], idB);
        if (index < 0) return 0;
        index <<= 1;
        return pair[index + 1];
    }

    public static int getWordID(String a) {
        return CoreDictionary.trie.exactMatchSearch(a);
    }
}
