package org.xm.xmnlp.hanlp.recognition;

import org.xm.xmnlp.hanlp.HanLP;
import org.xm.xmnlp.hanlp.algorithm.Viterbi;
import org.xm.xmnlp.hanlp.corpus.tag.NT;
import org.xm.xmnlp.hanlp.corpus.tag.Nature;
import org.xm.xmnlp.hanlp.dictionary.item.EnumItem;
import org.xm.xmnlp.hanlp.dictionary.nt.OrganizationDictionary;
import org.xm.xmnlp.hanlp.seg.common.Vertex;
import org.xm.xmnlp.hanlp.seg.common.WordNet;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author xuming
 */
public class OrganizationRecognition {
    public static Boolean recognition(List<Vertex> vertexList, WordNet wordNetOptimum, WordNet wordNetAll) {
        List<EnumItem<NT>> roleTagList = roleTag(vertexList, wordNetAll);
        if (HanLP.Config.DEBUG) {
            StringBuilder sbLog = new StringBuilder();
            Iterator<Vertex> iterator = vertexList.iterator();
            for (EnumItem<NT> NTEnumItem : roleTagList) {
                sbLog.append('[');
                sbLog.append(iterator.next().realWord);
                sbLog.append(' ');
                sbLog.append(NTEnumItem);
                sbLog.append(']');
            }
            System.out.printf("机构名角色观察：%s\n", sbLog.toString());
        }
        List<NT> NTList = viterbiExCompute(roleTagList);
        if (HanLP.Config.DEBUG) {
            StringBuilder sbLog = new StringBuilder();
            Iterator<Vertex> iterator = vertexList.iterator();
            sbLog.append('[');
            for (NT NT : NTList) {
                sbLog.append(iterator.next().realWord);
                sbLog.append('/');
                sbLog.append(NT);
                sbLog.append(" ,");
            }
            if (sbLog.length() > 1)
                sbLog.delete(sbLog.length() - 2, sbLog.length());
            sbLog.append(']');
            System.out.printf("机构名角色标注：%s\n", sbLog.toString());
        }
        OrganizationDictionary.parsePattern(NTList, vertexList, wordNetOptimum, wordNetAll);
        return true;
    }


    private static List<EnumItem<NT>> roleTag(List<Vertex> vertexList, WordNet wordNetAll) {
        List<EnumItem<NT>> tagList = new LinkedList<>();
        for (Vertex vertex : vertexList) {
            Nature nature = vertex.guessNature();
            switch (nature) {
                case nz: {
                    if (vertex.getAttribute().totalFrequency <= 1000) {
                        tagList.add(new EnumItem<>(NT.F, 1000));
                    } else {
                        break;
                    }
                }
                continue;
                case ni:
                case nic:
                case nis:
                case nit: {
                    EnumItem<NT> ntEnumItem = new EnumItem<>(NT.K, 1000);
                    ntEnumItem.addLabel(NT.D, 1000);
                    tagList.add(ntEnumItem);
                }
                continue;
                case m: {
                    EnumItem<NT> ntEnumItem = new EnumItem<>(NT.M, 1000);
                    tagList.add(ntEnumItem);
                }
                continue;
            }
            EnumItem<NT> ntEnumItem = OrganizationDictionary.dictionary.get(vertex.word);
            if (ntEnumItem == null) {
                ntEnumItem = new EnumItem<>(NT.Z, OrganizationDictionary.transformMatrixDictionary.getTotalFrequency(NT.Z));
            }
            tagList.add(ntEnumItem);
        }
        return tagList;
    }

    public static List<NT> viterbiExCompute(List<EnumItem<NT>> roleTagList) {
        return Viterbi.computeEnum(roleTagList, OrganizationDictionary.transformMatrixDictionary);
    }
}
