package org.xm.xmnlp.hanlp.suggest.scorer.lexeme;

import org.xm.xmnlp.hanlp.algorithm.ArrayCompare;
import org.xm.xmnlp.hanlp.algorithm.ArrayDistance;
import org.xm.xmnlp.hanlp.dictionary.CoreSynonymDictionaryEx;
import org.xm.xmnlp.hanlp.suggest.scorer.ISentenceKey;
import org.xm.xmnlp.hanlp.tokenizer.IndexTokenizer;

import java.util.Iterator;
import java.util.List;

/**
 * @author xuming
 */
public class IdVector implements Comparable<IdVector>, ISentenceKey<IdVector> {
    public List<Long[]> idArrayList;

    public IdVector(List<Long[]> idArrayList) {
        this.idArrayList = idArrayList;
    }

    public IdVector(String sentence) {
        this(CoreSynonymDictionaryEx.convert(IndexTokenizer.segment(sentence), false));
    }

    @Override
    public int compareTo(IdVector o) {
        int len1 = idArrayList.size();
        int len2 = o.idArrayList.size();
        int min = Math.min(len1, len2);
        Iterator<Long[]> iterator1 = idArrayList.iterator();
        Iterator<Long[]> iterator2 = o.idArrayList.iterator();

        int k = 0;
        while (k < min) {
            Long[] c1 = iterator1.next();
            Long[] c2 = iterator2.next();
            if (ArrayDistance.computeMinimumDistance(c1, c2) != 0) {
                return ArrayCompare.compare(c1, c2);
            }
            ++k;
        }
        return len1 - len2;
    }

    @Override
    public Double similarity(IdVector other) {
        Double score = 0.0;
        for (Long[] a : idArrayList) {
            for (Long[] b : other.idArrayList) {
                Long distance = ArrayDistance.computeAverageDistance(a, b);
                score += 1.0 / (0.1 + distance);
            }
        }
        return score / other.idArrayList.size();
    }
}
