package org.xm.xmnlp.hanlp.suggest.scorer;

import java.util.Map;

/**
 * @author xuming
 */
public interface IScorer {
    Map<String,Double> computeScore(String outerSentence);
    void addSentence(String sentence);
    void removeAllSentences();
}
