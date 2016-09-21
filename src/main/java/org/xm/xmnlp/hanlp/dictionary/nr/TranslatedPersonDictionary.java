package org.xm.xmnlp.hanlp.dictionary.nr;

import org.xm.xmnlp.hanlp.HanLP;
import org.xm.xmnlp.hanlp.collection.trie.DoubleArrayTrie;
import org.xm.xmnlp.hanlp.corpus.io.IOUtil;
import org.xm.xmnlp.hanlp.utility.Predefine;

import java.io.BufferedReader;
import java.util.Map;
import java.util.TreeMap;

import static org.xm.xmnlp.hanlp.utility.Predefine.logger;

/**
 * @author xuming
 */
public class TranslatedPersonDictionary {
    public static final String path = HanLP.Config.TranslatedPersonDictionaryPath;
    static DoubleArrayTrie<Boolean> trie;
    static {
        long start = System.currentTimeMillis();
        if(!load()){
            throw new IllegalArgumentException("音译人名词典" + path + "加载失败");
        }
        logger.info("音译人名词典" + path + "加载成功，耗时" + (System.currentTimeMillis() - start) + "ms");

    }
    public static boolean load(){
        trie = new DoubleArrayTrie<>();
        if(loadDat())return true;
        try{
            BufferedReader br = IOUtil.newBufferedReader(path);
            String line ;
            TreeMap<String,Boolean> map = new TreeMap<>();
            TreeMap<Character,Integer> charFrequencyMap = new TreeMap<>();
            while((line = br.readLine()) !=null){
                map.put(line,true);
                for(char c : line.toCharArray()){
                    if ("不赞".indexOf(c) >= 0) continue;
                    Integer f = charFrequencyMap.get(c);
                    if(f == null) f= 0;
                    charFrequencyMap.put(c,f+1);
                }
            }
            br.close();
            map.put(String.valueOf('.'),true);
            for(Map.Entry<Character,Integer> entry: charFrequencyMap.entrySet()){
                if(entry.getValue() <10)continue;
                map.put(String.valueOf(entry.getKey()),true);
            }
            logger.info("音译人名词典" + path + "开始构建双数组……");
            trie.build(map);
            logger.info("音译人名词典" + path + "开始编译DAT文件……");
            logger.info("音译人名词典" + path + "编译结果：" + saveDat(map));
        }catch (Exception e){
            logger.severe("自定义词典" + path + "读取错误！" + e);
            return false;
        }

        return true;
    }
    private static boolean saveDat(TreeMap<String,Boolean> map ){
        return trie.save(path + Predefine.BIN_EXT);
    }
    private static boolean loadDat(){
        return trie.load(path + Predefine.TRIE_EXT);
    }
    public static boolean containsKey(String key){
        return trie.containsKey(key);
    }
    public static boolean containsKey(String key ,int length){
        if(!trie.containsKey(key))return false;
        return key.length() >= length;
    }
}
