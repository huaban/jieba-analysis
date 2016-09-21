package org.xm.xmnlp.hanlp.recognition.ns;

import org.xm.xmnlp.hanlp.HanLP;
import org.xm.xmnlp.hanlp.algorithm.Viterbi;
import org.xm.xmnlp.hanlp.corpus.tag.NS;
import org.xm.xmnlp.hanlp.corpus.tag.Nature;
import org.xm.xmnlp.hanlp.dictionary.item.EnumItem;
import org.xm.xmnlp.hanlp.dictionary.ns.PlaceDictionary;
import org.xm.xmnlp.hanlp.seg.common.Vertex;
import org.xm.xmnlp.hanlp.seg.common.WordNet;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * @author xuming
 */
public class PlaceRecognition {
    public static boolean recognition(List<Vertex> pWordSegResult, WordNet wordNetOptimum, WordNet wordNetAll) {
        List<EnumItem<NS>> roleTagList = roleTag(pWordSegResult, wordNetAll);
        if (HanLP.Config.DEBUG) {
            StringBuilder sbLog = new StringBuilder();
            Iterator<Vertex> iterator = pWordSegResult.iterator();
            for (EnumItem<NS> NSEnumItem : roleTagList) {
                sbLog.append('[');
                sbLog.append(iterator.next().realWord);
                sbLog.append(' ');
                sbLog.append(NSEnumItem);
                sbLog.append(']');
            }
            System.out.printf("地名角色观察：%s\n", sbLog.toString());
        }
        List<NS> NSList = viterbiExCompute(roleTagList);
        if (HanLP.Config.DEBUG) {
            StringBuilder sbLog = new StringBuilder();
            Iterator<Vertex> iterator = pWordSegResult.iterator();
            sbLog.append('[');
            for (NS NS : NSList) {
                sbLog.append(iterator.next().realWord);
                sbLog.append('/');
                sbLog.append(NS);
                sbLog.append(" ,");
            }
            if (sbLog.length() > 1) sbLog.delete(sbLog.length() - 2, sbLog.length());
            sbLog.append(']');
            System.out.printf("地名角色标注：%s\n", sbLog.toString());
        }

        PlaceDictionary.parsePattern(NSList, pWordSegResult, wordNetOptimum, wordNetAll);
        return true;
    }

    public static List<EnumItem<NS>> roleTag(List<Vertex> vertexList, WordNet wordNetAll) {
        List<EnumItem<NS>> tagList = new LinkedList<EnumItem<NS>>();
        ListIterator<Vertex> listIterator = vertexList.listIterator();
//        int line = 0;
        while (listIterator.hasNext()) {
            Vertex vertex = listIterator.next();
            // 构成更长的
//            if (Nature.ns == vertex.getNature() && vertex.getAttribute().totalFrequency <= 1000)
//            {
//                String value = vertex.realWord;
//                int longestSuffixLength = PlaceSuffixDictionary.dictionary.getLongestSuffixLength(value);
//                int wordLength = value.length() - longestSuffixLength;
//                if (longestSuffixLength != 0 && wordLength != 0)
//                {
//                    listIterator.remove();
//                    for (int l = 0, tag = NS.D.ordinal(); l < wordLength; ++l, ++tag)
//                    {
//                        listIterator.add(wordNetAll.getFirst(line + l));
//                        tagList.add(new EnumItem<>(NS.values()[tag], 1000));
//                    }
//                    listIterator.add(wordNetAll.get(line + wordLength, longestSuffixLength));
//                    tagList.add(new EnumItem<>(NS.H, 1000));
//                    line += vertex.realWord.length();
//                    continue;
//                }
//            }
            if (Nature.ns == vertex.getNature() && vertex.getAttribute().totalFrequency <= 1000) {
                if (vertex.realWord.length() < 3)               // 二字地名，认为其可以再接一个后缀或前缀
                    tagList.add(new EnumItem<NS>(NS.H, NS.G));
                else
                    tagList.add(new EnumItem<NS>(NS.G));        // 否则只可以再加后缀
                continue;
            }
            EnumItem<NS> NSEnumItem = PlaceDictionary.dictionary.get(vertex.word);  // 此处用等效词，更加精准
            if (NSEnumItem == null) {
                NSEnumItem = new EnumItem<NS>(NS.Z, PlaceDictionary.transformMatrixDictionary.getTotalFrequency(NS.Z));
            }
            tagList.add(NSEnumItem);
//            line += vertex.realWord.length();
        }
        return tagList;
    }

    private static void insert(ListIterator<Vertex> listIterator, List<EnumItem<NS>> tagList, WordNet wordNetAll, int line, NS ns) {
        Vertex vertex = wordNetAll.getFirst(line);
        assert vertex != null : "全词网居然有空白行！";
        listIterator.add(vertex);
        tagList.add(new EnumItem<NS>(ns, 1000));
    }

    /**
     * 维特比算法求解最优标签
     *
     * @param roleTagList
     * @return
     */
    public static List<NS> viterbiExCompute(List<EnumItem<NS>> roleTagList) {
        return Viterbi.computeEnum(roleTagList, PlaceDictionary.transformMatrixDictionary);
    }
}
