package org.xm.xmnlp.word.corpus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xm.xmnlp.word.segmentation.Word;
import org.xm.xmnlp.word.util.DoubleArrayGenericTrie;
import org.xm.xmnlp.word.util.WordConfTools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mingzai on 2016/9/11.
 */
public class Bigram {
    private static final Logger LOGGER = LoggerFactory.getLogger(Bigram.class);
    private  static final DoubleArrayGenericTrie DOUBLE_ARRAY_GENERIC_TRIE = new DoubleArrayGenericTrie(WordConfTools.getInt("bigram.double.array.trie.size",5300000));
    private static int maxFrequency = 0;
    static {
        reload();
    }
    public static void reload(){

    }
    public static void clear(){
        DOUBLE_ARRAY_GENERIC_TRIE.clear();
    }
    public static void load(List<String> lines){
        LOGGER.info("init bigram");
        Map<String,Integer> map = new HashMap<>();
        for(String line : lines){
            try{
                addLine(line,map);
            }catch (Exception e){
                LOGGER.error("error bigram data:"+line);
            }
        }
        int size = map.size();
        DOUBLE_ARRAY_GENERIC_TRIE.putAll(map);
        LOGGER.info("init bigram finished,bigram data cout num:" +size);
    }

    private static void addLine(String line, Map<String, Integer> map) {

    }


    public static Map<List<Word>, Float> process(List<Word>[] sentences) {
        return null;
    }

    public static float getScore(String text, String text1) {
        return 0;
    }
}
