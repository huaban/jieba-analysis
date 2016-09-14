package org.xm.xmnlp.word.segmentation.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xm.xmnlp.word.corpus.Bigram;
import org.xm.xmnlp.word.corpus.Trigram;
import org.xm.xmnlp.word.dictionary.Dictionary;
import org.xm.xmnlp.word.dictionary.DictionaryFactory;
import org.xm.xmnlp.word.recognition.PersonName;
import org.xm.xmnlp.word.recognition.Punctuation;
import org.xm.xmnlp.word.segmentation.DictionaryBasedSegmentation;
import org.xm.xmnlp.word.segmentation.Word;
import org.xm.xmnlp.word.segmentation.WordRefiner;
import org.xm.xmnlp.word.util.WordConfTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by xuming
 */
public abstract class AbstractSegmentation implements DictionaryBasedSegmentation {
    protected final Logger LOGGER = LoggerFactory.getLogger(AbstractSegmentation.class);
    private static final boolean PERSON_NAME_RECOGNIZE = WordConfTools.getBoolean("person.name.recognize", true);
    private static final boolean KEEP_WHITESPACE = WordConfTools.getBoolean("keep.punctuation", false);
    private static final boolean PARALLEL_SEG = WordConfTools.getBoolean("parallel.seg", true);
    private static final int INTERCEPT_LENGTH = WordConfTools.getInt("intercept.length", 16);
    private static final String NGRAM = WordConfTools.get("ngram", "bigram");
    private static Dictionary dictionary = DictionaryFactory.getDictionary();

    public boolean isParallelSeg() {
        return PARALLEL_SEG;
    }

    public void setDictionary(Dictionary dictionary) {
        this.dictionary.clear();
        this.dictionary = dictionary;
    }

    public Dictionary getDictionary() {
        return dictionary;
    }

    public abstract List<Word> segImpl(String text);

    public boolean ngramEnabled() {
        return "bigram".equals(NGRAM) || "trigram".equals(NGRAM);
    }

    public Map<List<Word>, Float> ngram(List<Word>... sentences) {
        if ("bigram".equals(NGRAM)) {
            return Bigram.process(sentences);
        }
        if ("trigram".equals(NGRAM)) {
            return Trigram.process(sentences);
        }
        return null;
    }

    public int getInterceptLength() {
        if (getDictionary().getMaxLength() > INTERCEPT_LENGTH) {
            return getDictionary().getMaxLength();
        }
        return INTERCEPT_LENGTH;
    }

    public List<Word> seg(String text) {
        List<Word> words = segDefault(text);
        words = WordRefiner.process(words);
        return words;
    }

    public List<Word> segDefault(String text) {
        List<String> sentences = Punctuation.seg(text, KEEP_WHITESPACE);
        if (sentences.size() == 1) {
            return segSentence(sentences.get(0));
        }
        if (!PARALLEL_SEG) {
            return sentences.stream()
                    .flatMap(sentence -> segSentence(sentence).stream()).collect(Collectors.toList());
        }
        Map<Integer, String> sentenceMap = new HashMap<>();
        int len = sentences.size();
        for (int i = 0; i < len; i++) {
            sentenceMap.put(i, sentences.get(i));
        }
        List<Word>[] results = new List[sentences.size()];
        sentenceMap.entrySet().parallelStream()
                .forEach(entry -> {
                    int index = entry.getKey();
                    String sentence = entry.getValue();
                    results[index] = segSentence(sentence);
                });
        sentences.clear();
        sentenceMap.clear();
        List<Word> resultList = new ArrayList<>();
        for (List<Word> result : results) {
            if (result == null || result.isEmpty()) {
                continue;
            }
            resultList.addAll(result);
        }
        return resultList;
    }

    private List<Word> segSentence(final String sentence) {
        if (sentence.length() == 1) {
            if (KEEP_WHITESPACE) {
                List<Word> result = new ArrayList<>(1);
                result.add(new Word(sentence));
                return result;
            } else {
                if (!Character.isWhitespace(sentence.charAt(0))) {
                    List<Word> result = new ArrayList<>(1);
                    result.add(new Word(sentence));
                    return result;
                }
            }
        }
        if (sentence.length() > 1) {
            List<Word> list = segImpl(sentence);
            if (list != null) {
                if (PERSON_NAME_RECOGNIZE) {
                    list = PersonName.recognize(list);
                }
                return list;
            } else {
                LOGGER.error("text:" + sentence + " no segmentation result");
            }
        }
        return null;
    }

    public static void main(String[] args) {
//        Segmentation englishSeg = new AbstractSegmentation() {
//            @Override
//            public List<Word> segImpl(String text) {
//                List<Word> words = new ArrayList<>();
//                for(String word: text.split("\\s+")){
//                    words.add(new Word(word));
//                }
//                return words;
//            }
//        };
//        System.out.println(englishSeg.seg("i love programming "));
    }
}
