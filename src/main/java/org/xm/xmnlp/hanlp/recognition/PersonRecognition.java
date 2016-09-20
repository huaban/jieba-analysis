package org.xm.xmnlp.hanlp.recognition;

import org.xm.xmnlp.hanlp.HanLP;
import org.xm.xmnlp.hanlp.algorithm.Viterbi;
import org.xm.xmnlp.hanlp.corpus.tag.NR;
import org.xm.xmnlp.hanlp.dictionary.item.EnumItem;
import org.xm.xmnlp.hanlp.dictionary.nr.PersonDictionary;
import org.xm.xmnlp.hanlp.seg.common.Vertex;
import org.xm.xmnlp.hanlp.seg.common.WordNet;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author xuming
 */
public class PersonRecognition {

    public static boolean recognition(List<Vertex> vertexList, WordNet wordNetOptimum, WordNet wordNetAll) {
        List<EnumItem<NR>> roleTagList = roleObserve(vertexList);
        if (HanLP.Config.DEBUG) {
            StringBuilder sb = new StringBuilder();
            Iterator<Vertex> iterator = vertexList.iterator();
            for (EnumItem<NR> nrEnumItem : roleTagList) {
                sb.append('[');
                sb.append(iterator.next().realWord);
                sb.append(' ');
                sb.append(nrEnumItem);
                sb.append(']');
            }
            System.out.printf("人名角色观察：%s\n", sb.toString());
        }
        List<NR> nrList = viterbiComputeSimply(roleTagList);
        if (HanLP.Config.DEBUG) {
            StringBuilder sb = new StringBuilder();
            Iterator<Vertex> iterator = vertexList.iterator();
            sb.append('[');
            for (NR nr : nrList) {
                sb.append(iterator.next().realWord);
                sb.append('/');
                sb.append(nr);
                sb.append(" ,");
            }
            if (sb.length() > 1) sb.delete(sb.length() - 2, sb.length());
            sb.append(']');
            System.out.printf("人名角色标注：%s\n", sb.toString());
        }
        PersonDictionary.parsePattern(nrList, vertexList, wordNetOptimum, wordNetAll);
        return true;
    }

    public static List<EnumItem<NR>> roleObserve(List<Vertex> wordSegResult) {
        List<EnumItem<NR>> tagList = new LinkedList<>();
        for (Vertex vertex : wordSegResult) {
            EnumItem<NR> nrEnumItem = PersonDictionary.dictionary.get(vertex.realWord);
            if (nrEnumItem == null) {
                switch (vertex.guessNature()) {
                    case nr: {
                        if (vertex.getAttribute().totalFrequency <= 1000 && vertex.realWord.length() == 2) {
                            nrEnumItem = new EnumItem<NR>(NR.X, NR.G);
                        } else {
                            nrEnumItem = new EnumItem<NR>(NR.A, PersonDictionary.transformMatrixDictionary.getTotalFrequency(NR.A));
                        }
                    }
                    break;
                    case nnt:
                        nrEnumItem = new EnumItem<NR>(NR.G, NR.K);
                        break;
                    default:
                        nrEnumItem = new EnumItem<NR>(NR.A, PersonDictionary.transformMatrixDictionary.getTotalFrequency(NR.A));
                        break;
                }
            }
            tagList.add(nrEnumItem);
        }
        return tagList;
    }

    /**
     * 维特比算法求解最优标签
     *
     * @param roleTagList
     * @return
     */
    public static List<NR> viterbiCompute(List<EnumItem<NR>> roleTagList) {
        return Viterbi.computeEnum(roleTagList, PersonDictionary.transformMatrixDictionary);
    }

    /**
     * 简化的"维特比算法"求解最优标签
     *
     * @param roleTagList
     * @return
     */
    public static List<NR> viterbiComputeSimply(List<EnumItem<NR>> roleTagList) {
        return Viterbi.computeEnumSimply(roleTagList, PersonDictionary.transformMatrixDictionary);
    }


}
