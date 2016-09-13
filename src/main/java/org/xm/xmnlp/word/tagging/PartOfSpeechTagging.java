package org.xm.xmnlp.word.tagging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xm.xmnlp.word.segmentation.Word;
import org.xm.xmnlp.word.util.DictionaryUtil;
import org.xm.xmnlp.word.util.GenericTrie;

import java.util.List;

/**
 * Created by xuming
 */
public class PartOfSpeechTagging {

    private static final Logger LOGGER = LoggerFactory.getLogger(PartOfSpeechTagging.class);
    private static final GenericTrie<String> GENERIC_TRIE = new GenericTrie<>();
    static {
        reload();
    }
    private static final String PATH = "/part_of_speech_dic.txt";
    public static void reload() {
        if (GENERIC_TRIE==null ) {
            load(DictionaryUtil.loadDictionaryFile(PATH));
        }
    }

    public static void clear(){
        GENERIC_TRIE.clear();
    }
    public static void load(List<String> lines){
        LOGGER.info("init pos tag");
        int count = 0;
        for(String line : lines){
            add(line);
        }
        LOGGER.info("init pos tag finished. count num:"+count);
    }
    public static void add(String line){
        try{
            String[] attr = line.split(":");
            GENERIC_TRIE.put(attr[0],attr[1]);
        }catch (Exception e){
            LOGGER.error("error pos tag data:"+line);
        }
    }
    public static void remove(String line){
        try {
            String[] attr = line.split(":");
            GENERIC_TRIE.remove(attr[0]);
        }catch (Exception e){
            LOGGER.error("error pos tag data:"+line);
        }
    }


    private PartOfSpeechTagging(){
    }

    public static void process(List<Word> word) {

    }
}
