package org.xm.xmnlp.hanlp.seg.CRF;

import org.xm.xmnlp.hanlp.HanLP;
import org.xm.xmnlp.hanlp.algorithm.Viterbi;
import org.xm.xmnlp.hanlp.collection.trie.bintrie.BinTrie;
import org.xm.xmnlp.hanlp.corpus.tag.Nature;
import org.xm.xmnlp.hanlp.dictionary.CoreDictionary;
import org.xm.xmnlp.hanlp.dictionary.CoreDictionaryTransformMatrixDictionary;
import org.xm.xmnlp.hanlp.dictionary.other.CharTable;
import org.xm.xmnlp.hanlp.model.CRFSegmentModel;
import org.xm.xmnlp.hanlp.model.crf.CRFModel;
import org.xm.xmnlp.hanlp.model.crf.FeatureFunction;
import org.xm.xmnlp.hanlp.model.crf.Table;
import org.xm.xmnlp.hanlp.seg.CharacterBasedGenerativeModelSegment;
import org.xm.xmnlp.hanlp.seg.Segment;
import org.xm.xmnlp.hanlp.seg.common.Term;
import org.xm.xmnlp.hanlp.seg.common.Vertex;
import org.xm.xmnlp.hanlp.utility.CharacterHelper;
import org.xm.xmnlp.hanlp.utility.GlobalObjectPool;

import java.util.*;

import static org.xm.xmnlp.hanlp.utility.Predefine.logger;

/**
 * @author xuming
 */
public class CRFSegment extends CharacterBasedGenerativeModelSegment {
    private CRFModel crfModel;

    public CRFSegment(String modelPath) {
        CRFModel crfModel = GlobalObjectPool.get(modelPath);
        if (crfModel != null) {
            this.crfModel = crfModel;
            return;
        }
        logger.info("CRF分词模型正在加载 " + modelPath);
        long start = System.currentTimeMillis();
        this.crfModel = CRFModel.loadTxt(modelPath, new CRFSegmentModel(new BinTrie<FeatureFunction>()));
        if (this.crfModel == null) {
            String error = "CRF model load" + modelPath + "failure,speed " + (System.currentTimeMillis() - start) + " ms";
            logger.severe(error);
            throw new IllegalArgumentException(error);
        } else {
            logger.info("CRF分词模型加载 " + modelPath + " 成功，耗时 " + (System.currentTimeMillis() - start) + " ms");
        }
        GlobalObjectPool.put(modelPath, this.crfModel);
    }

    public CRFSegment() {
        this(HanLP.Config.CRFSegmentModelPath);
    }

    public static String[][] atomSegmentToTable(char[] sentence) {
        String table[][] = new String[sentence.length][3];
        int size = 0;
        final int maxLen = sentence.length - 1;
        final StringBuilder sbAtom = new StringBuilder();
        out:
        for (int i = 0; i < sentence.length; i++) {
            if (sentence[i] >= '0' && sentence[i] <= '9') {
                sbAtom.append(sentence[i]);
                if (i == maxLen) {
                    table[size][0] = "M";
                    table[size][1] = sbAtom.toString();
                    ++size;
                    sbAtom.setLength(0);
                    break;
                }
                char c = sentence[++i];
                while (c == '.' || c == '%' || (c >= '0' && c <= '9')) {
                    sbAtom.append(sentence[i]);
                    if (i == maxLen) {
                        table[size][0] = "M";
                        table[size][1] = sbAtom.toString();
                        ++size;
                        sbAtom.setLength(0);
                        break out;
                    }
                    c = sentence[++i];
                }
                table[size][0] = "M";
                table[size][1] = sbAtom.toString();
                ++size;
                sbAtom.setLength(0);
                --i;
            } else if (CharacterHelper.isEnglishLetter(sentence[i]) || sentence[i] == ' ') {
                sbAtom.append(sentence[i]);
                if (i == maxLen) {
                    table[size][0] = "W";
                    table[size][1] = sbAtom.toString();
                    ++size;
                    sbAtom.setLength(0);
                    break;
                }
                char c = sentence[++i];
                while (CharacterHelper.isEnglishLetter(c) || c == ' ') {
                    sbAtom.append(sentence[i]);
                    if (i == maxLen) {
                        table[size][0] = "W";
                        table[size][1] = sbAtom.toString();
                        ++size;
                        sbAtom.setLength(0);
                        break out;
                    }
                    c = sentence[++i];
                }
                table[size][0] = "W";
                table[size][1] = sbAtom.toString();
                ++size;
                sbAtom.setLength(0);
                --i;
            } else {
                table[size][0] = table[size][1] = String.valueOf(sentence[i]);
                ++size;
            }
        }

        return resizeArray(table, size);
    }

    @Override
    protected List<Term> segSentence(char[] sentence) {
        if (sentence.length == 0) return Collections.emptyList();
        char[] sentenceConverted = CharTable.convert(sentence);
        Table table = new Table();
        table.v = atomSegmentToTable(sentenceConverted);
        crfModel.tag(table);
        List<Term> termList = new LinkedList<>();
        if (HanLP.Config.DEBUG) {
            System.out.println("CRF标注结果");
            System.out.println(table);
        }
        int offset = 0;
        OUTER:
        for (int i = 0; i < table.v.length; offset += table.v[i][1].length(), ++i) {
            String[] line = table.v[i];
            switch (line[2].charAt(0)) {
                case 'B': {
                    int begin = offset;
                    while (table.v[i][2].charAt(0) != 'E') {
                        offset += table.v[i][1].length();
                        ++i;
                        if (i == table.v.length) {
                            break;
                        }
                    }
                    if (i == table.v.length) {
                        termList.add(new Term(new String(sentence, begin, offset - begin), null));
                        break OUTER;
                    } else
                        termList.add(new Term(new String(sentence, begin, offset - begin + table.v[i][1].length()), null));
                }
                break;
                default: {
                    termList.add(new Term(new String(sentence, offset, table.v[i][1].length()), null));
                }
                break;
            }
        }
        if (config.speechTagging) {
            List<Vertex> vertexList = toVertexList(termList, true);
            Viterbi.compute(vertexList, CoreDictionaryTransformMatrixDictionary.transformMatrixDictionary);
            int i = 0;
            for (Term term : termList) {
                if (term.nature != null) term.nature = vertexList.get(i + 1).guessNature();
                ++i;
            }
        }
        if (config.useCustomDictionary) {
            List<Vertex> vertexList = toVertexList(termList, false);
            combineByCustomDictionary(vertexList);
            termList = toTermList(vertexList, config.offset);
        }
        return termList;
    }

    private static List<Vertex> toVertexList(List<Term> termList, boolean appendStart) {
        ArrayList<Vertex> vertexList = new ArrayList<>(termList.size() + 1);
        if (appendStart) vertexList.add(Vertex.B);
        for (Term term : termList) {
            CoreDictionary.Attribute attribute = CoreDictionary.get(term.word);
            if (attribute == null) {
                if (term.word.trim().length() == 0) {
                    attribute = new CoreDictionary.Attribute(Nature.x);
                } else {
                    attribute = new CoreDictionary.Attribute(Nature.x);
                }
            } else {
                term.nature = attribute.nature[0];
            }
            Vertex vertex = new Vertex(term.word, attribute);
            vertexList.add(vertex);
        }
        return vertexList;
    }

    /**
     * 将一条路径转为最终结果
     *
     * @param vertexList
     * @param offsetEnabled 是否计算offset
     * @return
     */
    protected static List<Term> toTermList(List<Vertex> vertexList, boolean offsetEnabled) {
        assert vertexList != null;
        int length = vertexList.size();
        List<Term> resultList = new ArrayList<Term>(length);
        Iterator<Vertex> iterator = vertexList.iterator();
        if (offsetEnabled) {
            int offset = 0;
            for (int i = 0; i < length; ++i) {
                Vertex vertex = iterator.next();
                Term term = convert(vertex);
                term.offset = offset;
                offset += term.length();
                resultList.add(term);
            }
        } else {
            for (int i = 0; i < length; ++i) {
                Vertex vertex = iterator.next();
                Term term = convert(vertex);
                resultList.add(term);
            }
        }
        return resultList;
    }

    /**
     * 将节点转为term
     *
     * @param vertex
     * @return
     */
    private static Term convert(Vertex vertex) {
        return new Term(vertex.realWord, vertex.guessNature());
    }

    /**
     * 数组减肥，原子分词可能会导致表格比原来的短
     *
     * @param array
     * @param size
     * @return
     */
    private static String[][] resizeArray(String[][] array, int size) {
        String[][] nArray = new String[size][];
        System.arraycopy(array, 0, nArray, 0, size);
        return nArray;
    }

    @Override
    public Segment enableNumberQuantifierRecognize(boolean enable) {
        throw new UnsupportedOperationException("暂不支持");
    }

}
