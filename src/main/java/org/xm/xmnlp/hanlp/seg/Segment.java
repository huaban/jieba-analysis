package org.xm.xmnlp.hanlp.seg;

import org.xm.xmnlp.hanlp.HanLP;
import org.xm.xmnlp.hanlp.collection.trie.DoubleArrayTrie;
import org.xm.xmnlp.hanlp.collection.trie.bintrie.BaseNode;
import org.xm.xmnlp.hanlp.corpus.tag.Nature;
import org.xm.xmnlp.hanlp.dictionary.CoreDictionary;
import org.xm.xmnlp.hanlp.dictionary.CustomDictionary;
import org.xm.xmnlp.hanlp.dictionary.other.CharTable;
import org.xm.xmnlp.hanlp.dictionary.other.CharType;
import org.xm.xmnlp.hanlp.seg.NShort.AtomNode;
import org.xm.xmnlp.hanlp.seg.common.Term;
import org.xm.xmnlp.hanlp.seg.common.Vertex;
import org.xm.xmnlp.hanlp.seg.common.WordNet;
import org.xm.xmnlp.hanlp.utility.Predefine;
import org.xm.xmnlp.hanlp.utility.SentencesUtil;
import org.xm.xmnlp.hanlp.utility.TextUtility;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import static org.xm.xmnlp.hanlp.corpus.tag.Nature.q;
import static org.xm.xmnlp.hanlp.utility.Predefine.logger;

/**
 * @author xuming
 */
public abstract class Segment {
    protected Config config;

    public Segment() {
        config = new Config();
    }

    protected static List<AtomNode> quickAtomSegment(char[] charArray, int start, int end) {
        List<AtomNode> atomNodeList = new LinkedList<>();
        int offsetAtom = start;
        int preType = CharType.get(charArray[offsetAtom]);
        int curType;
        while (++offsetAtom < end) {
            curType = CharType.get(charArray[offsetAtom]);
            if (curType != preType) {
                if (charArray[offsetAtom] == '.' && preType == CharType.CT_NUM) {
                    while (++offsetAtom < end) {
                        curType = CharType.get(charArray[offsetAtom]);
                        if (curType != CharType.CT_NUM) break;
                    }
                }
                atomNodeList.add(new AtomNode(new String(charArray, start, offsetAtom - start), preType));
                start = offsetAtom;
            }
            preType = curType;
        }
        if (offsetAtom == end) {
            atomNodeList.add(new AtomNode(new String(charArray, start, offsetAtom - start), preType));
        }
        return atomNodeList;
    }

    protected static List<Vertex> combineByCustomDictionary(List<Vertex> vertexList) {
        Vertex[] wordNet = new Vertex[vertexList.size()];
        vertexList.toArray(wordNet);
        // DAT合并
        DoubleArrayTrie<CoreDictionary.Attribute> dat = CustomDictionary.dat;
        for (int i = 0; i < wordNet.length; ++i) {
            int state = 1;
            state = dat.transition(wordNet[i].realWord, state);
            if (state > 0) {
                int to = i + 1;
                int end = to;
                CoreDictionary.Attribute value = dat.output(state);
                for (; to < wordNet.length; ++to) {
                    state = dat.transition(wordNet[to].realWord, state);
                    if (state < 0) break;
                    CoreDictionary.Attribute output = dat.output(state);
                    if (output != null) {
                        value = output;
                        end = to + 1;
                    }
                }
                if (value != null) {
                    combineWords(wordNet,i,end,value);
                    i = end - 1;
                }
            }
        }
        if (CustomDictionary.trie != null) {
            for (int i = 0; i < wordNet.length; ++i) {
                if (wordNet[i] == null) continue;
                BaseNode<CoreDictionary.Attribute> state = CustomDictionary.trie.transition(wordNet[i].realWord.toCharArray(), 0);
                if (state != null) {
                    int to = i + 1;
                    int end = to;
                    CoreDictionary.Attribute value = state.getValue();
                    for (; to < wordNet.length; ++to) {
                        if (wordNet[to] == null) continue;
                        state = state.transition(wordNet[to].realWord.toCharArray(), 0);
                        if (state == null) break;
                        if (state.getValue() != null) {
                            value = state.getValue();
                            end = to + 1;
                        }
                    }
                    if (value != null) {
                        combineWords(wordNet,i,end,value);
                        i = end - 1;
                    }
                }
            }
        }
        vertexList.clear();
        for (Vertex vertex : wordNet) {
            if (vertex != null) vertexList.add(vertex);
        }
        return vertexList;
    }

    private static void combineWords(Vertex[] wordNet, int start, int end, CoreDictionary.Attribute attribute) {
        if (start + 1 == end) {
            wordNet[start].attribute = attribute;
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = start; i < end; ++i) {
                sb.append(wordNet[i]);
                wordNet[i] = null;
            }
            wordNet[start] = new Vertex(sb.toString(), attribute);
        }
    }

    protected void mergeNumberQuantifier(List<Vertex> termList, WordNet wordNetAll, Config config) {
        if (termList.size() < 4) return;
        StringBuilder sbQuantifier = new StringBuilder();
        ListIterator<Vertex> iterator = termList.listIterator();
        iterator.next();
        int line = 1;
        while (iterator.hasNext()) {
            Vertex pre = iterator.next();
            if (pre.hasNature(Nature.m)) {
                sbQuantifier.append(pre.realWord);
                Vertex cur = null;
                while (iterator.hasNext() && (cur = iterator.next()).hasNature(Nature.m)) {
                    sbQuantifier.append(cur.realWord);
                    iterator.remove();
                    removeFromWordNet(cur, wordNetAll, line, sbQuantifier.length());
                }
                if (cur != null) {
                    if ((cur.hasNature(q) || cur.hasNature(Nature.qv) || cur.hasNature(Nature.qt))) {
                        if (config.indexMode) {
                            wordNetAll.add(line, new Vertex(sbQuantifier.toString(), new CoreDictionary.Attribute(Nature.m)));
                        }
                        sbQuantifier.append(cur.realWord);
                        iterator.remove();
                        removeFromWordNet(cur, wordNetAll, line, sbQuantifier.length());
                    } else {
                        line += cur.realWord.length();
                    }
                }
                if (sbQuantifier.length() != pre.realWord.length()) {
                    setNumberQuantifier(sbQuantifier, pre);
                }
            }
            sbQuantifier.setLength(0);
            line += pre.realWord.length();
        }
    }

    private void setNumberQuantifier(StringBuilder sbQuantifier, Vertex pre) {
        pre.realWord = sbQuantifier.toString();
        pre.word = Predefine.TAG_NUMBER;
        pre.attribute = new CoreDictionary.Attribute(Nature.mq);
        pre.wordID = CoreDictionary.M_WORD_ID;
        sbQuantifier.setLength(0);
    }

    private static void removeFromWordNet(Vertex cur, WordNet wordNetAll, int line, int length) {
        LinkedList<Vertex>[] vertexes = wordNetAll.getVertexes();
        for (Vertex vertex : vertexes[line + length]) {
            if (vertex.from == cur) {
                vertex.from = null;
            }
        }
        ListIterator<Vertex> iterator = vertexes[line + length - cur.realWord.length()].listIterator();
        while (iterator.hasNext()) {
            Vertex vertex = iterator.next();
            if (vertex == cur) {
                iterator.remove();
            }
        }
    }

    public List<Term> seg(String text) {
        char[] charArray = text.toCharArray();
        if (HanLP.Config.Normalization) {
            CharTable.normalization(charArray);
        }
        if (config.threadNumber > 1 && charArray.length > 10000) {
            List<String> sentenceList = SentencesUtil.toSentenceList(charArray);
            String[] sentenceArray = new String[sentenceList.size()];
            sentenceList.toArray(sentenceArray);
            List<Term>[] termListArray = new List[sentenceArray.length];
            final int per = sentenceArray.length / config.threadNumber;
            WorkThread[] threadArray = new WorkThread[config.threadNumber];
            for (int i = 0; i < config.threadNumber - 1; ++i) {
                int from = i * per;
                threadArray[i] = new WorkThread(sentenceArray, termListArray, from, from + per);
                threadArray[i].start();
//                System.out.println("0:"+i);
            }
            threadArray[config.threadNumber - 1] = new WorkThread(sentenceArray, termListArray, (config.threadNumber - 1) * per, sentenceArray.length);
            threadArray[config.threadNumber - 1].start();
//            System.out.println("1:"+(config.threadNumber - 1));
            try {
                for (WorkThread thread : threadArray) {
                    thread.join();

//                    System.out.println("2:");
                }
            } catch (InterruptedException e) {
                logger.severe("线程同步异常：" + TextUtility.exceptionToString(e));
                return Collections.emptyList();
            }
            List<Term> termList = new LinkedList<>();
            if (config.offset || config.indexMode) {
                int sentenceOffset = 0;
                for (int i = 0; i < sentenceArray.length; ++i) {
                    for (Term term : termListArray[i]) {
                        term.offset += sentenceOffset;
                        termList.add(term);
                    }
                    sentenceOffset += sentenceArray[i].length();
                }
            } else {
                for (List<Term> list : termListArray) {
                    termList.addAll(list);
                }
            }
            return termList;
        }
        return segSentence(charArray);
    }

    protected abstract List<Term> segSentence(char[] sentence);

    public List<Term> seg(char[] text) {
        assert text != null;
         if (HanLP.Config.Normalization) {
            CharTable.normalization(text);
        }
        return segSentence(text);
    }

    class WorkThread extends Thread {
        String[] sentenceArray;
        List<Term>[] termListArray;
        int from;
        int to;

        public WorkThread(String[] sentenceArray, List<Term>[] termListArray, int from, int to) {
            this.sentenceArray = sentenceArray;
            this.termListArray = termListArray;
            this.from = from;
            this.to = to;
        }

        @Override
        public void run() {
            for (int i = from; i < to; ++i) {
                termListArray[i] = segSentence(sentenceArray[i].toCharArray());
            }
        }
    }

    public List<List<Term>> seg2sentence(String text) {
        List<List<Term>> resultList = new LinkedList<>();
        for (String sentence : SentencesUtil.toSentenceList(text)) {
            resultList.add(segSentence(sentence.toCharArray()));
        }
        return resultList;
    }

    public Segment enableIndexMode(boolean enable) {
        config.indexMode = enable;
        return this;
    }

    public Segment enablePartOfSpeechTagging(boolean enable) {
        config.speechTagging = enable;
        return this;
    }

    public Segment enableNameRecognize(boolean enable) {
        config.nameRecognize = enable;
        config.updateNerConfig();
        return this;
    }

    public Segment enablePlaceRecognize(boolean enable) {
        config.placeRecognize = enable;
        config.updateNerConfig();
        return this;
    }

    public Segment enableOrganizationRecognize(boolean enable) {
        config.organizationRecognize = enable;
        config.updateNerConfig();
        return this;
    }

    public Segment enableCustomDictionary(boolean enable) {
        config.useCustomDictionary = enable;
        return this;
    }

    public Segment enableTranslatedNameRecognize(boolean enable) {
        config.translatedNameRecognize = enable;
        config.updateNerConfig();
        return this;
    }

    public Segment enableJapanesenameRecognize(boolean enable) {
        config.japaneseNameRecognize = enable;
        config.updateNerConfig();
        return this;
    }

    public Segment enableOffset(boolean enable) {
        config.offset = enable;
        return this;
    }

    public Segment enableNumberQuantifierRecognize(boolean enable) {
        config.numberQuantifierRecognize = enable;
        return this;
    }

    public Segment enableAllNamedEntityRecognize(boolean enable) {
        config.nameRecognize = enable;
        config.japaneseNameRecognize = enable;
        config.translatedNameRecognize = enable;
        config.placeRecognize = enable;
        config.organizationRecognize = enable;
        config.updateNerConfig();
        return this;
    }

    public Segment enableMultithreading(boolean enable) {
        if (enable) config.threadNumber = 4;
        else config.threadNumber = 1;
        return this;
    }

    public Segment enableMultithreading(int threadNumber) {
        config.threadNumber = threadNumber;
        return this;
    }
}

