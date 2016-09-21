package org.xm.xmnlp.hanlp.recognition.nr;

import org.xm.xmnlp.hanlp.HanLP;
import org.xm.xmnlp.hanlp.corpus.tag.Nature;
import org.xm.xmnlp.hanlp.dictionary.CoreDictionary;
import org.xm.xmnlp.hanlp.dictionary.nr.TranslatedPersonDictionary;
import org.xm.xmnlp.hanlp.seg.common.Vertex;
import org.xm.xmnlp.hanlp.seg.common.WordNet;
import org.xm.xmnlp.hanlp.utility.Predefine;

import java.util.List;
import java.util.ListIterator;

import static org.xm.xmnlp.hanlp.dictionary.nr.NRConstant.WORD_ID;

/**
 * @author xuming
 */
public class TranslatedPersonRecognition {
    public static void recognition(List<Vertex> segReult, WordNet wordNetOptimum, WordNet wordNetAll) {
        StringBuilder sbName = new StringBuilder();
        int appendTimes = 0;
        ListIterator<Vertex> listIterator = segReult.listIterator();
        listIterator.next();
        int line = 1;
        int activeLine = 1;
        while (listIterator.hasNext()) {
            Vertex vertex = listIterator.next();
            if (appendTimes > 0) {
                if (vertex.guessNature() == Nature.nrf || TranslatedPersonDictionary.containsKey(vertex.realWord)) {
                    sbName.append(vertex.realWord);
                    ++appendTimes;
                } else {
                    if (appendTimes > 1) {
                        if (HanLP.Config.DEBUG) {
                            System.out.println("音译人名识别出：" + sbName.toString());
                        }
                        wordNetOptimum.insert(activeLine, new Vertex(Predefine.TAG_PEOPLE, sbName.toString(), new CoreDictionary.Attribute(Nature.nrf), WORD_ID), wordNetAll);
                    }
                    sbName.setLength(0);
                    appendTimes = 0;
                }
            } else {
                if (vertex.guessNature() == Nature.nrf || vertex.getNature() == Nature.nsf) {
                    sbName.append(vertex.realWord);
                    ++appendTimes;
                    activeLine = line;
                }
            }
            line += vertex.realWord.length();
        }
    }
}
