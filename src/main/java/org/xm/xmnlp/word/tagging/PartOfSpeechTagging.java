package org.xm.xmnlp.word.tagging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xm.xmnlp.word.segmentation.Word;
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
    public static void reload(){

    }
    public void clear(){
        GENERIC_TRIE.clear();
    }


    private PartOfSpeechTagging(){
    }

    public static void process(List<Word> word) {

    }
}
