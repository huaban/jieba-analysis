package org.xm.xmnlp.word.recognition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xm.xmnlp.word.segmentation.Word;
import org.xm.xmnlp.word.util.DictionaryUtil;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by mingzai on 2016/9/11.
 */
public class StopWord {
    private static final Logger LOGGER = LoggerFactory.getLogger(StopWord.class);
    private static final Set<String> stopwords = new HashSet<>();
    public static Set<String> getStopwords() {
        return stopwords;
    }
    private static final String PATH = "/stopwords.txt";
    static {
        reload();
    }

    public static void reload() {
        if (stopwords == null||stopwords.isEmpty()) {
            load(DictionaryUtil.loadDictionaryFile(PATH));
        }

    }

    public static void load(List<String> lines) {
        LOGGER.info("初始化停用词");
        for(String line : lines){
            if(!isStopChar(line)){
                stopwords.add(line);
            }
        }
        LOGGER.info("停用词初始化完毕，停用词个数："+stopwords.size());
    }

    private static boolean isStopChar(String word) {
        if (word.length() == 1) {
            char ch = word.charAt(0);
            if (ch < 48) {
                return true;
            }
            if (ch > 57 && ch < 19968) {
                return true;
            }
            if (ch > 40869) {
                return true;
            }
        }
        return false;
    }

    public static boolean isStopWord(String word) {
        if (word == null) {
            return false;
        }
        word = word.trim();
        return isStopChar(word) || stopwords.contains(word);
    }

    public static void filterStopWords(List<Word> words) {
        Iterator<Word> iter = words.iterator();
        while (iter.hasNext()) {
            Word word = iter.next();
            if (isStopWord(word.getText())) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("remove stopword:" + word.getText());
                }
                iter.remove();
            }
        }
    }

    public static void main(String[] args) {
        LOGGER.info("stop word:");
        int i = 1;
        for (String w : stopwords) {
            LOGGER.info((i++) + ": " + w);
        }
    }

}

