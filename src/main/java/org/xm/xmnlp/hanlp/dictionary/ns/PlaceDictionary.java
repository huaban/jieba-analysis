package org.xm.xmnlp.hanlp.dictionary.ns;

import org.xm.xmnlp.hanlp.HanLP;
import org.xm.xmnlp.hanlp.collection.AhoCorasick.AhoCorasickDoubleArrayTrie;
import org.xm.xmnlp.hanlp.corpus.tag.NS;
import org.xm.xmnlp.hanlp.dictionary.CoreDictionary;
import org.xm.xmnlp.hanlp.dictionary.TransformMatrixDictionary;
import org.xm.xmnlp.hanlp.dictionary.item.EnumItem;
import org.xm.xmnlp.hanlp.seg.common.Vertex;
import org.xm.xmnlp.hanlp.seg.common.WordNet;
import org.xm.xmnlp.hanlp.utility.Predefine;

import java.util.List;
import java.util.TreeMap;

import static org.xm.xmnlp.hanlp.utility.Predefine.logger;

/**
 * @author xuming
 */
public class PlaceDictionary {
    /**
     * 地名词典
     */
    public static NSDictionary dictionary;
    /**
     * 转移矩阵词典
     */
    public static TransformMatrixDictionary<NS> transformMatrixDictionary;
    /**
     * AC算法用到的Trie树
     */
    public static AhoCorasickDoubleArrayTrie<String> trie;

    /**
     * 本词典专注的词的ID
     */
    static final int WORD_ID = CoreDictionary.getWordID(Predefine.TAG_PLACE);
    /**
     * 本词典专注的词的属性
     */
    static final CoreDictionary.Attribute ATTRIBUTE = CoreDictionary.get(WORD_ID);

    static {
        long start = System.currentTimeMillis();
        dictionary = new NSDictionary();
        dictionary.load(HanLP.Config.PlaceDictionaryPath);
        logger.info(HanLP.Config.PlaceDictionaryPath + "加载成功，耗时" + (System.currentTimeMillis() - start) + "ms");
        transformMatrixDictionary = new TransformMatrixDictionary<NS>(NS.class);
        transformMatrixDictionary.load(HanLP.Config.PlaceDictionaryTrPath);
        trie = new AhoCorasickDoubleArrayTrie<String>();
        TreeMap<String, String> patternMap = new TreeMap<String, String>();
        patternMap.put("CH", "CH");
        patternMap.put("CDH", "CDH");
        patternMap.put("CDEH", "CDEH");
        patternMap.put("GH", "GH");
        trie.build(patternMap);
    }

    /**
     * 模式匹配
     *
     * @param nsList         确定的标注序列
     * @param vertexList     原始的未加角色标注的序列
     * @param wordNetOptimum 待优化的图
     * @param wordNetAll
     */
    public static void parsePattern(List<NS> nsList, List<Vertex> vertexList, final WordNet wordNetOptimum, final WordNet wordNetAll) {
//        ListIterator<Vertex> listIterator = vertexList.listIterator();
        StringBuilder sbPattern = new StringBuilder(nsList.size());
        for (NS ns : nsList) {
            sbPattern.append(ns.toString());
        }
        String pattern = sbPattern.toString();
        final Vertex[] wordArray = vertexList.toArray(new Vertex[0]);
        trie.parseText(pattern, new AhoCorasickDoubleArrayTrie.IHit<String>() {
            @Override
            public void hit(int begin, int end, String value) {
                StringBuilder sbName = new StringBuilder();
                for (int i = begin; i < end; ++i) {
                    sbName.append(wordArray[i].realWord);
                }
                String name = sbName.toString();
                // 对一些bad case做出调整
                if (isBadCase(name)) return;

                // 正式算它是一个名字
                if (HanLP.Config.DEBUG) {
                    System.out.printf("识别出地名：%s %s\n", name, value);
                }
                int offset = 0;
                for (int i = 0; i < begin; ++i) {
                    offset += wordArray[i].realWord.length();
                }
                wordNetOptimum.insert(offset, new Vertex(Predefine.TAG_PLACE, name, ATTRIBUTE, WORD_ID), wordNetAll);
            }
        });
    }

    /**
     * 因为任何算法都无法解决100%的问题，总是有一些bad case，这些bad case会以“盖公章 A 1”的形式加入词典中<BR>
     * 这个方法返回是否是bad case
     *
     * @param name
     * @return
     */
    static boolean isBadCase(String name) {
        EnumItem<NS> nrEnumItem = dictionary.get(name);
        if (nrEnumItem == null) return false;
        return nrEnumItem.containsLabel(NS.Z);
    }
}
