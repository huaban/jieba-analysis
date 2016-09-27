package org.xm.xmnlp.hanlp.suggest.scorer.pinyin;

import org.xm.xmnlp.hanlp.suggest.scorer.BaseScorer;

/**
 * @author xuming
 */
public class PinyinScorer extends BaseScorer<PinyinKey> {
    @Override
    protected PinyinKey generateKey(String sentence) {
        PinyinKey pinyinKey = new PinyinKey(sentence);
        if (pinyinKey.size() == 0) return null;
        return pinyinKey;
    }
}
