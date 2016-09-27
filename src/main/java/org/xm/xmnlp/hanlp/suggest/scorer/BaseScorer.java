package org.xm.xmnlp.hanlp.suggest.scorer;

import java.util.*;

/**
 * 基本打分器
 *
 * @param <T> 这是储存器map中key的类型，具有相同key的句子会存入同一个entry
 * @author xuming
 */
public abstract class BaseScorer<T extends ISentenceKey> implements IScorer {
    protected Map<T, Set<String>> storage;
    public double boost = 1.0;

    public BaseScorer() {
        storage = new TreeMap<>();
    }

    @Override
    public Map<String, Double> computeScore(String outerSentence) {
        TreeMap<String, Double> result = new TreeMap<>(Collections.reverseOrder());
        T keyOuter = generateKey(outerSentence);
        if (keyOuter == null) return result;
        for (Map.Entry<T, Set<String>> entry : storage.entrySet()) {
            T key = entry.getKey();
            Double score = keyOuter.similarity(key);
            for (String sentence : entry.getValue()) {
                result.put(sentence, score);
            }
        }
        return result;
    }

    @Override
    public void addSentence(String sentence) {
        T key = generateKey(sentence);
        if (key == null) return;
        Set<String> set = storage.get(key);
        if (set == null) {
            set = new TreeSet<>();
            storage.put(key, set);
        }
        set.add(sentence);
    }

    protected abstract T generateKey(String sentence);

    @Override
    public void removeAllSentences() {
        storage.clear();
    }
}
