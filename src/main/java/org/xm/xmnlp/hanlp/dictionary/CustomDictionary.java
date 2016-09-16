package org.xm.xmnlp.hanlp.dictionary;

import org.xm.xmnlp.hanlp.HanLP;
import org.xm.xmnlp.hanlp.collection.trie.DoubleArrayTrie;
import org.xm.xmnlp.hanlp.corpus.tag.Nature;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.TreeMap;

import static org.xm.xmnlp.hanlp.dictionary.CoreDictionary.loadDat;
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
        if (!loadMainDictionary(path[0])) {
            logger.warning("自定义词典" + Arrays.toString(path) + "加载失败");
        } else {
            logger.info("自定义词典加载成功:" + dat.size() + "个词条，耗时" + (System.currentTimeMillis() - start) + "ms");
        }
    }
    private static boolean loadMainDictionary(String mainPath){
        logger.info("load custom dictionary:"+mainPath);
        if(loadDat(mainPath))return true;
        TreeMap<String,CoreDictionary.Attribute> map = new TreeMap<>();
        LinkedHashSet<Nature> customNatureCollector = new LinkedHashSet<>();
        try{
            for(String p:path){

            }
        }
    }
}
