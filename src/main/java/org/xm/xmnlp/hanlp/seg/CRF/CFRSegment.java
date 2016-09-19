package org.xm.xmnlp.hanlp.seg.CRF;

import org.xm.xmnlp.hanlp.model.crf.CRFModel;
import org.xm.xmnlp.hanlp.seg.CharacterBasedGenerativeModelSegment;
import org.xm.xmnlp.hanlp.seg.common.Term;

import java.util.List;

/**
 * @author xuming
 */
public class CFRSegment extends CharacterBasedGenerativeModelSegment {
    private CRFModel crfModel;



    @Override
    protected List<Term> segSentence(char[] sentence) {
        return null;
    }
}
