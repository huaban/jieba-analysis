package org.xm.xmnlp.hanlp.tokenizer;

import org.xm.xmnlp.hanlp.HanLP;
import org.xm.xmnlp.hanlp.seg.Segment;
import org.xm.xmnlp.hanlp.seg.common.Term;

import java.util.List;

/**
 * @author xuming
 */
public class NLPTokenizer {
    public static final Segment SEGMENT = HanLP.newSegment()
            .enableNameRecognize(true)
            .enableTranslatedNameRecognize(true)
            .enableJapanesenameRecognize(true)
            .enablePlaceRecognize(true)
            .enableOrganizationRecognize(true)
            .enablePartOfSpeechTagging(true);
    public static List<Term> segment(String text){
        return SEGMENT.seg(text.toCharArray());
    }
    public static List<Term> segment(char[] text) {
        return SEGMENT.seg(text);
    }
    public static List<List<Term>> seg2sentence(String text)
    {
        return SEGMENT.seg2sentence(text);
    }

}
