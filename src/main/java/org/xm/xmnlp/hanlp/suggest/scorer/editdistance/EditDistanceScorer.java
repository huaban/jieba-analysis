package org.xm.xmnlp.hanlp.suggest.scorer.editdistance;

import org.xm.xmnlp.hanlp.suggest.scorer.BaseScorer;

/**
 * @author xuming
 */
public class EditDistanceScorer extends BaseScorer<CharArray> {
    @Override
    protected CharArray generateKey(String sentence) {
        char[] charArray = sentence.toCharArray();
        if (charArray.length == 0) return null;
        return new CharArray(charArray);
    }
}
