package org.xm.xmnlp.word.recognition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xm.xmnlp.word.segmentation.Word;
import org.xm.xmnlp.word.util.WordConfTools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by mingzai on 2016/9/11.
 */
public class StopWord {
    private static final Logger LOGGER = LoggerFactory.getLogger(StopWord.class);

    private static final Set<String> stopwords = new HashSet<>();

    static {
        reload();
    }

    public static void reload() {
        if (stopwords == null||stopwords.isEmpty()) {
            loadStopWord();
        }

    }

    public static void loadStopWord() {
        LOGGER.info("初始化停用词");
        String str = WordConfTools.get("stopwords.path", "classpath:stopwords.txt");
        String path = str.replace("classpath:", "/stopwords.txt");
        InputStream is = StopWord.class.getResourceAsStream(path);
        BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
        try {
            long start = System.currentTimeMillis();
            while (br.ready()) {
                String line = br.readLine().trim();
                if (line.isEmpty()) {
                    continue;
                }
                if (!isStopChar(line)) {
                    stopwords.add(line);
                }
            }
            LOGGER.info(String.format(Locale.getDefault(), "stopword dict load finished,spend %d ms,count num:%d ",
                    System.currentTimeMillis() - start,stopwords.size()));

        } catch (IOException e) {
            LOGGER.info("stopword dict load failure!" + path);
        } finally {
            try {
                if (null != br) br.close();
                if (null != is) is.close();
            } catch (IOException e) {
                LOGGER.info(String.format(Locale.getDefault(), "%s close failure !", path));
            }
        }
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

