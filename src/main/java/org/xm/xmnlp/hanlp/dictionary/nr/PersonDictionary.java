package org.xm.xmnlp.hanlp.dictionary.nr;

import org.xm.xmnlp.hanlp.HanLP;
import org.xm.xmnlp.hanlp.collection.AhoCorasick.AhoCorasickDoubleArrayTrie;
import org.xm.xmnlp.hanlp.corpus.tag.NR;
import org.xm.xmnlp.hanlp.corpus.tag.Nature;
import org.xm.xmnlp.hanlp.dictionary.CoreDictionary;
import org.xm.xmnlp.hanlp.dictionary.TransformMatrixDictionary;
import org.xm.xmnlp.hanlp.dictionary.item.EnumItem;
import org.xm.xmnlp.hanlp.seg.common.Vertex;
import org.xm.xmnlp.hanlp.seg.common.WordNet;
import org.xm.xmnlp.hanlp.utility.Predefine;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.TreeMap;

import static org.xm.xmnlp.hanlp.corpus.tag.NR.B;
import static org.xm.xmnlp.hanlp.dictionary.nr.NRConstant.WORD_ID;
import static org.xm.xmnlp.hanlp.utility.Predefine.logger;

/**
 * @author xuming
 */
public class PersonDictionary {
    public static NRDictionary dictionary;
    public static TransformMatrixDictionary<NR> transformMatrixDictionary;
    public static AhoCorasickDoubleArrayTrie<NRPattern> trie;
    public static final CoreDictionary.Attribute ATTRIBUTE = new CoreDictionary.Attribute(Nature.nr, 100);

    static {
        long start = System.currentTimeMillis();
        dictionary = new NRDictionary();
        if (!dictionary.load(HanLP.Config.PersonDictionaryPath)) {
            System.err.println("人名词典加载失败：" + HanLP.Config.PersonDictionaryPath);
            System.exit(-1);
        }
        transformMatrixDictionary = new TransformMatrixDictionary<>(NR.class);
        transformMatrixDictionary.load(HanLP.Config.PersonDictionaryTrPath);
        trie = new AhoCorasickDoubleArrayTrie<>();
        TreeMap<String, NRPattern> map = new TreeMap<>();
        for (NRPattern pattern : NRPattern.values()) {
            map.put(pattern.toString(), pattern);
        }
        trie.build(map);
        logger.info(HanLP.Config.PersonDictionaryPath + "加载成功，耗时" + (System.currentTimeMillis() - start) + "ms");
    }

    public static void parsePattern(List<NR> nrList, List<Vertex> vertexList, final WordNet wordNetOptimum, final WordNet wordNetAll) {
        ListIterator<Vertex> listIterator = vertexList.listIterator();
        StringBuilder sbPattern = new StringBuilder(nrList.size());
        NR preNR = NR.A;
        boolean backUp = false;
        int index = 0;
        for (NR nr : nrList) {
            ++index;
            Vertex current = listIterator.next();
            switch (nr) {
                case U:
                    if (!backUp) {
                        vertexList = new ArrayList<>(vertexList);
                        listIterator = vertexList.listIterator(index);
                        backUp = true;
                    }
                    sbPattern.append(NR.K.toString());
                    sbPattern.append(B.toString());
                    preNR = B;
                    listIterator.previous();
                    String nowK = current.realWord.substring(0, current.realWord.length() - 1);
                    String nowB = current.realWord.substring(current.realWord.length() - 1);
                    listIterator.set(new Vertex(nowK));
                    listIterator.next();
                    listIterator.add(new Vertex(nowB));
                    continue;
                case V:
                    if (!backUp) {
                        vertexList = new ArrayList<>(vertexList);
                        listIterator = vertexList.listIterator(index);
                        backUp = true;
                    }
                    if (preNR == B) {
                        sbPattern.append(NR.E.toString());
                    } else {
                        sbPattern.append(NR.D.toString());
                    }
                    sbPattern.append(NR.L.toString());
                    listIterator.previous();
                    String nowED = current.realWord.substring(current.realWord.length() - 1);
                    String nowL = current.realWord.substring(0, current.realWord.length() - 1);
                    listIterator.set(new Vertex(nowED));
                    listIterator.add(new Vertex(nowL));
                    listIterator.next();
                    continue;
                default:
                    sbPattern.append(nr.toString());
                    break;

            }
            preNR = nr;
        }
        String pattern = sbPattern.toString();
        final Vertex[] wordArray = vertexList.toArray(new Vertex[0]);
        final int[] offsetArray = new int[wordArray.length];
        offsetArray[0] = 0;
        for (int i = 1; i < wordArray.length; ++i) {
            offsetArray[i] = offsetArray[i - 1] + wordArray[i - 1].realWord.length();
        }
        trie.parseText(pattern, new AhoCorasickDoubleArrayTrie.IHit<NRPattern>() {
            @Override
            public void hit(int begin, int end, NRPattern value) {
//            logger.trace("匹配到：{}", keyword);
                StringBuilder sbName = new StringBuilder();
                for (int i = begin; i < end; ++i) {
                    sbName.append(wordArray[i].realWord);
                }
                String name = sbName.toString();
//            logger.trace("识别出：{}", name);
                // 对一些bad case做出调整
                switch (value) {
                    case BCD:
                        if (name.charAt(0) == name.charAt(2)) return; // 姓和最后一个名不可能相等的
//                        String cd = name.substring(1);
//                        if (CoreDictionary.contains(cd))
//                        {
//                            EnumItem<NR> item = PersonDictionary.dictionary.get(cd);
//                            if (item == null || !item.containsLabel(Z)) return; // 三字名字但是后两个字不在词典中，有很大可能性是误命中
//                        }
                        break;
                }
                if (isBadCase(name)) return;

                // 正式算它是一个名字
                if (HanLP.Config.DEBUG) {
                    System.out.printf("识别出人名：%s %s\n", name, value);
                }
                int offset = offsetArray[begin];
                wordNetOptimum.insert(offset, new Vertex(Predefine.TAG_PEOPLE, name, ATTRIBUTE, WORD_ID), wordNetAll);
            }
        });
    }

    /**
     * 因为任何算法都无法解决100%的问题，总是有一些bad case，这些bad case会以“盖公章 A 1”的形式加入词典中<BR>
     * 这个方法返回人名是否是bad case
     *
     * @param name
     * @return
     */
    static boolean isBadCase(String name) {
        EnumItem<NR> nrEnumItem = dictionary.get(name);
        if (nrEnumItem == null) return false;
        return nrEnumItem.containsLabel(NR.A);
    }
}
