package org.xm.xmnlp.word.corpus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xm.xmnlp.word.segmentation.Word;
import org.xm.xmnlp.word.util.DictionaryUtil;
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
    private static final DoubleArrayGenericTrie DOUBLE_ARRAY_GENERIC_TRIE = new DoubleArrayGenericTrie(WordConfTools.getInt("bigram.double.array.trie.size", 5300000));
    private static int maxFrequency = 0;

    static {
        reload();
    }

    private static final String PATH = WordConfTools.get("bigram.path", "classpath:bigram.txt");

    public static void reload() {
        if (DOUBLE_ARRAY_GENERIC_TRIE == null) {
            load(DictionaryUtil.loadDictionaryFile(PATH));
        }
    }

    public static void clear() {
        DOUBLE_ARRAY_GENERIC_TRIE.clear();
    }

    public static void load(List<String> lines) {
        LOGGER.info("init bigram");
        Map<String, Integer> map = new HashMap<>();
        for (String line : lines) {
            try {
                addLine(line, map);
            } catch (Exception e) {
                LOGGER.error("error bigram data:" + line);
            }
        }
        int size = map.size();
        DOUBLE_ARRAY_GENERIC_TRIE.putAll(map);
        LOGGER.info("init bigram finished,bigram data cout num:" + size);
    }

    public static void add(String line) {
        throw new RuntimeException("not yet support method.");
    }

    private static void addLine(String line, Map<String, Integer> map) {
        String[] attr = line.split("\\s+");
        int frequency = Integer.parseInt(attr[1]);
        if (frequency > maxFrequency) {
            maxFrequency = frequency;
        }
        map.put(attr[0], frequency);
    }

    public static void remove(String line) {
        throw new RuntimeException("not support remove method.");
    }


    public static Map<List<Word>, Float> process(List<Word>... sentences) {
        Map<List<Word>, Float> map = new HashMap<>();
        Map<String, Float> bigramScores = new HashMap<>();
        Map<String, Float> twoBigramScores = new HashMap<>();
        for (List<Word> sentence : sentences) {
            if (map.get(sentence) != null) {
                continue;
            }
            float score = 0;
            if (sentence.size() > 1) {
                String last = "";
                for (int i = 0; i < sentence.size() - 1; i++) {
                    String first = sentence.get(i).getText();
                    String second = sentence.get(i + 1).getText();
                    float bigramScore = getScore(first, second);
                    if (bigramScore > 0) {
                        if (last.endsWith(first)) {
                            twoBigramScores.put(last + second, bigramScores.get(last) + bigramScore);
                            last = "";
                        }
                        last = first + second;
                        bigramScores.put(last, bigramScore);
                        score += bigramScore;
                    }
                }
            }
            map.put(sentence, score);
        }
        if (bigramScores.size() > 0 || twoBigramScores.size() > 0) {
            for (List<Word> sentence : map.keySet()) {
                for (Word word : sentence) {
                    Float bigramScore = bigramScores.get(word.getText());
                    Float twoBigramScore = twoBigramScores.get(word.getText());
                    Float[] array = {bigramScore, twoBigramScore};
                    for (Float score : array) {
                        if (score != null && score > 0) {
                            if (LOGGER.isDebugEnabled()) {
                                LOGGER.debug(word.getText() + " get score:" + score);
                            }
                            float value = map.get(sentence);
                            value += score;
                            map.put(sentence, value);
                        }
                    }
                }
            }
        }

        return map;
    }

    public static float sentenceScore(List<Word> words) {
        if (words.size() > 1) {
            float total = words.size() - 1;
            float match = 0;
            for (int i = 0; i < words.size() - 1; i++) {
                if (getScore(words.get(i).getText(), words.get(i + 1).getText()) > 0) {
                    match++;
                }
            }
            return match / total;
        }
        return 0;
    }

    public static float bigram(List<Word> words) {
        if (words.size() > 1) {
            float score = 0;
            for (int i = 0; i < words.size() - 1; i++) {
                score += getScore(words.get(i).getText(), words.get(i + 1).getText());
            }
            return score;
        }
        return 0;
    }

    public static float getScore(String first, String second) {
        int frequency = getFrequency(first, second);
        float score = frequency / (float) maxFrequency;
        if (LOGGER.isDebugEnabled()) {
            if (score > 0) {
                LOGGER.debug("bigram model " + first + ":" + second + " the score:" + score);
            }
        }
        return score;
    }

    public static int getFrequency(String firest, String second) {
        Integer value = DOUBLE_ARRAY_GENERIC_TRIE.get(firest + ":" + second);
        if (value == null || value < 0) {
            return 0;
        }
        return value;
    }
}
