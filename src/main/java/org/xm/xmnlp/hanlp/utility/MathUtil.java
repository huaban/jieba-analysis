package org.xm.xmnlp.hanlp.utility;

import org.xm.xmnlp.hanlp.dictionary.CoreBiGramTableDictionary;
import org.xm.xmnlp.hanlp.seg.common.Vertex;

import static org.xm.xmnlp.hanlp.utility.Predefine.MAX_FREQUENCY;
import static org.xm.xmnlp.hanlp.utility.Predefine.dSmoothingPara;
import static org.xm.xmnlp.hanlp.utility.Predefine.dTemp;

/**
 * @author xuming
 */
public class MathUtil {
    public static double calculateWeight(Vertex from, Vertex to) {
        int frequency = from.getAttribute().totalFrequency;
        if (frequency == 0) {
            frequency = 1;  // 防止发生除零错误
        }
        //int nTwoWordsFreq = BiGramDictionary.getBiFrequency(from.word, to.word);
        int nTwoWordsFreq = CoreBiGramTableDictionary.getBiFrequency(from.wordID, to.wordID);
        double value = -Math.log(dSmoothingPara * frequency / (MAX_FREQUENCY) + (1 - dSmoothingPara) * ((1 - dTemp) * nTwoWordsFreq / frequency + dTemp));
        if (value < 0.0) {
            value = -value;
        }
        return value;
    }
}
