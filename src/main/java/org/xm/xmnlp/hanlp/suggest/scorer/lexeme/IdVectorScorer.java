package org.xm.xmnlp.hanlp.suggest.scorer.lexeme;

import org.xm.xmnlp.hanlp.suggest.scorer.BaseScorer;

/**
 * 单词语义向量打分器
 * @author xuming
 */
public class IdVectorScorer extends BaseScorer<IdVector> {
    @Override
    protected IdVector generateKey(String sentence) {
        IdVector idVector = new IdVector(sentence);
        if (idVector.idArrayList.size() == 0) return null;
        return idVector;
    }
}
