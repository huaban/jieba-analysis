package org.xm.xmnlp.hanlp.seg.Viterbi;

import org.xm.xmnlp.hanlp.HanLP;
import org.xm.xmnlp.hanlp.recognition.OrganizationRecognition;
import org.xm.xmnlp.hanlp.recognition.PersonRecognition;
import org.xm.xmnlp.hanlp.seg.WordBasedGenerativeModelSegment;
import org.xm.xmnlp.hanlp.seg.common.Term;
import org.xm.xmnlp.hanlp.seg.common.Vertex;
import org.xm.xmnlp.hanlp.seg.common.WordNet;

import java.util.LinkedList;
import java.util.List;

/**
 * @author xuming
 */
public class ViterbiSegment extends WordBasedGenerativeModelSegment {


    @Override
    protected List<Term> segSentence(char[] sentence) {
        WordNet wordNetAll = new WordNet(sentence);
        GenerateWordNet(wordNetAll);
        if (HanLP.Config.DEBUG) {
            System.out.printf("粗分词网：\n%s\n", wordNetAll);
        }
        List<Vertex> vertexList = viterbi(wordNetAll);
        if (config.useCustomDictionary) {
            combineByCustomDictionary(vertexList);
        }
        if (HanLP.Config.DEBUG) {
            System.out.println("粗分结果" + convert(vertexList, false));
        }
        if (config.numberQuantifierRecognize) {
            mergeNumberQuantifier(vertexList, wordNetAll, config);
        }
        if (config.ner) {
            WordNet wordNetOptimum = new WordNet(sentence, vertexList);
            int preSize = wordNetOptimum.size();
            if (config.nameRecognize) {
                PersonRecognition.recognition(vertexList, wordNetOptimum, wordNetAll);
            }
            /*if (config.translatedNameRecognize) {
                TransformMatrixDictionary.recognition(vertexList, wordNetOptimum, wordNetAll);
            }
            if (config.japaneseNameRecognize) {
                JapanesePersonRecognition.recognition(vertexList, wordNetOptimum, wordNetAll);
            }
            if (config.placeRecognize) {
                PlaceRecognition.recognition(vertexList, wordNetOptimum, wordNetAll);
            }*/
            if (config.organizationRecognize) {
                // 层叠隐马模型——生成输出作为下一级隐马输入
                vertexList = viterbi(wordNetOptimum);
                wordNetOptimum.clear();
                wordNetOptimum.addAll(vertexList);
                preSize = wordNetOptimum.size();
                OrganizationRecognition.recognition(vertexList, wordNetOptimum, wordNetAll);
            }
            if (wordNetOptimum.size() != preSize) {
                vertexList = viterbi(wordNetOptimum);
                if (HanLP.Config.DEBUG) {
                    System.out.printf("细分词网：\n%s\n", wordNetOptimum);
                }
            }
        }
        // 如果是索引模式则全切分
        if (config.indexMode) {
            return getIndexResult(vertexList, wordNetAll);
        }

        // 是否标注词性
        if (config.speechTagging) {
            speechTagging(vertexList);
        }

        return convert(vertexList, config.offset);
    }

    private static List<Vertex> viterbi(WordNet wordNet) {
        LinkedList<Vertex> nodes[] = wordNet.getVertexes();
        LinkedList<Vertex> vertexList = new LinkedList<>();
        for (Vertex node : nodes[1]) {
            node.updateFrom(nodes[0].getFirst());
        }
        for (int i = 1; i < nodes.length - 1; ++i) {
            LinkedList<Vertex> nodeArray = nodes[i];
            if (nodeArray == null) continue;
            for (Vertex node : nodeArray) {
                if (node.from == null) continue;
                for (Vertex to : nodes[i + node.realWord.length()]) {
                    to.updateFrom(node);
                }
            }
        }
        Vertex from = nodes[nodes.length - 1].getFirst();
        while (from != null) {
            vertexList.addFirst(from);
            from = from.from;
        }
        return vertexList;
    }
}
