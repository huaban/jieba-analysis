package org.xm.xmnlp.hanlp.seg.NShort;

import org.xm.xmnlp.hanlp.HanLP;
import org.xm.xmnlp.hanlp.algorithm.Dijkstra;
import org.xm.xmnlp.hanlp.recognition.nr.JapanesePersonRecognition;
import org.xm.xmnlp.hanlp.recognition.nr.PersonRecognition;
import org.xm.xmnlp.hanlp.recognition.nr.TranslatedPersonRecognition;
import org.xm.xmnlp.hanlp.recognition.ns.PlaceRecognition;
import org.xm.xmnlp.hanlp.recognition.nt.OrganizationRecognition;
import org.xm.xmnlp.hanlp.seg.NShort.Path.NShortPath;
import org.xm.xmnlp.hanlp.seg.WordBasedGenerativeModelSegment;
import org.xm.xmnlp.hanlp.seg.common.Graph;
import org.xm.xmnlp.hanlp.seg.common.Term;
import org.xm.xmnlp.hanlp.seg.common.Vertex;
import org.xm.xmnlp.hanlp.seg.common.WordNet;

import java.util.LinkedList;
import java.util.List;

import static org.xm.xmnlp.hanlp.utility.Predefine.logger;

/**
 * @author xuming
 */
public class NShortSegment extends WordBasedGenerativeModelSegment {

    @Override
    protected List<Term> segSentence(char[] sentence) {
        WordNet wordNetOptimum = new WordNet(sentence);
        WordNet wordNetAll = new WordNet(sentence);
        List<List<Vertex>> coarseResult = BiSegment(2, wordNetOptimum, wordNetAll);
        boolean NERexists = false;
        for (List<Vertex> vertexList : coarseResult) {
            if (HanLP.Config.DEBUG) {
                System.out.println("粗分结果" + convert(vertexList, false));
            }
            if (config.ner) {
                wordNetOptimum.addAll(vertexList);
                int preSize = wordNetOptimum.size();
                if (config.nameRecognize) {
                    PersonRecognition.recognition(vertexList, wordNetOptimum, wordNetAll);
                }
                if (config.translatedNameRecognize) {
                    TranslatedPersonRecognition.recognition(vertexList, wordNetOptimum, wordNetAll);
                }
                if (config.japaneseNameRecognize) {
                    JapanesePersonRecognition.recognition(vertexList, wordNetOptimum, wordNetAll);
                }
                if (config.placeRecognize) {
                    PlaceRecognition.recognition(vertexList, wordNetOptimum, wordNetAll);
                }
                if (config.organizationRecognize) {
                    vertexList = Dijkstra.compute(GenerateBiGraph(wordNetOptimum));
                    wordNetOptimum.addAll(vertexList);
                    OrganizationRecognition.recognition(vertexList, wordNetOptimum, wordNetAll);
                }
                if (!NERexists && preSize != wordNetOptimum.size()) {
                    NERexists = true;
                }
            }
        }
        List<Vertex> vertexList = coarseResult.get(0);
        if (NERexists) {
            Graph graph = GenerateBiGraph(wordNetOptimum);
            vertexList = Dijkstra.compute(graph);
            if (HanLP.Config.DEBUG) {
                System.out.printf("细分词网：\n%s\n", wordNetOptimum);
                System.out.printf("细分词图：%s\n", graph.printByTo());
            }
        }
        if (config.numberQuantifierRecognize) {
            mergeNumberQuantifier(vertexList, wordNetAll, config);
        }
        if (config.indexMode) {
            return getIndexResult(vertexList, wordNetAll);
        }
        if (config.speechTagging) {
            speechTagging(vertexList);
        }
        if (config.useCustomDictionary) {
            combineByCustomDictionary(vertexList);
        }
        return convert(vertexList, config.offset);
    }

    public List<List<Vertex>> BiSegment(int nkind, WordNet wordNetOptimum, WordNet wordNetAll) {
        List<List<Vertex>> coarseResult = new LinkedList<>();
        ////////////////生成词网////////////////////
        GenerateWordNet(wordNetAll);
        logger.info("词网大小：" + wordNetAll.size());
        logger.info("打印词网：\n" + wordNetAll);
        ///////////////生成词图////////////////////
        Graph graph = GenerateBiGraph(wordNetAll);
        logger.info(graph.toString());
        if (HanLP.Config.DEBUG) {
            System.out.printf("打印词图：%s\n", graph.printByTo());
        }
        NShortPath nShortPath = new NShortPath(graph, nkind);
        List<int[]> spResult = nShortPath.getNPaths(nkind * 2);
        if (spResult.size() == 0) {
            throw new RuntimeException(nkind + "－最短路径求解失败，请检查上面的词网是否存在负圈或悬孤节点");
        }
        logger.info(nkind + "-最短路径");
        for (int[] path : spResult) {
            logger.info(Graph.parseResult(graph.parsePath(path)));
        }
        ////////////日期、数字合并策略
        for (int[] path : spResult) {
            List<Vertex> vertexList = graph.parsePath(path);
            GenerateWord(vertexList, wordNetOptimum);
            coarseResult.add(vertexList);
        }
        return coarseResult;
    }

    public static List<Term> parse(String text) {
        return new NShortSegment().seg(text);
    }

    public NShortSegment enablePartOfSpeechTagging(boolean enable) {
        config.speechTagging = enable;
        return this;
    }

    public NShortSegment enablePlaceRecognize(boolean enable) {
        config.placeRecognize = enable;
        config.updateNerConfig();
        return this;
    }

    public NShortSegment enableOrganizationRecognize(boolean enable) {
        config.organizationRecognize = enable;
        config.updateNerConfig();
        return this;
    }

    public NShortSegment enableTranslatedNameRecognize(boolean enable) {
        config.translatedNameRecognize = enable;
        config.updateNerConfig();
        return this;
    }

    public NShortSegment enableJanpanseseNameRecognize(boolean enable) {
        config.japaneseNameRecognize = enable;
        config.updateNerConfig();
        return this;
    }

    /**
     * 是否启用偏移量计算（开启后Term.offset才会被计算）
     *
     * @param enable
     * @return
     */
    public NShortSegment enableOffset(boolean enable) {
        config.offset = enable;
        return this;
    }

    public NShortSegment enableAllNamedEntityRecognize(boolean enable) {
        config.nameRecognize = enable;
        config.japaneseNameRecognize = enable;
        config.translatedNameRecognize = enable;
        config.placeRecognize = enable;
        config.organizationRecognize = enable;
        config.updateNerConfig();
        return this;
    }

}
