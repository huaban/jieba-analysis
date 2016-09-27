package org.xm.xmnlp.hanlp.dictionary;

import org.xm.xmnlp.hanlp.HanLP;
import org.xm.xmnlp.hanlp.algorithm.EditDistance;
import org.xm.xmnlp.hanlp.corpus.io.IOUtil;
import org.xm.xmnlp.hanlp.dictionary.common.CommonSynonymDictionary;
import org.xm.xmnlp.hanlp.dictionary.common.CommonSynonymDictionaryEx;
import org.xm.xmnlp.hanlp.dictionary.stopword.CoreStopWordDictionary;
import org.xm.xmnlp.hanlp.seg.common.Term;
import org.xm.xmnlp.hanlp.utility.TextUtility;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xuming
 */
public class CoreSynonymDictionaryEx {
    static CommonSynonymDictionaryEx dictionary;

    static {
        try {
            dictionary = CommonSynonymDictionaryEx.create(IOUtil.newInputStream(HanLP.Config.CoreSynonymDictionaryDictionaryPath));
        } catch (Exception e) {
            System.err.println("载入核心同义词词典失败" + e);
            System.exit(-1);
        }
    }

    public static Long[] get(String key) {
        return dictionary.get(key);
    }

    /**
     * 语义距离
     *
     * @param itemA
     * @param itemB
     * @return
     */
    public static long distance(CommonSynonymDictionary.SynonymItem itemA, CommonSynonymDictionary.SynonymItem itemB) {
        return itemA.distance(itemB);
    }

    /**
     * 将分词结果转换为同义词列表
     *
     * @param sentence          句子
     * @param withUndefinedItem 是否保留词典中没有的词语
     * @return
     */
    public static List<Long[]> convert(List<Term> sentence, boolean withUndefinedItem) {
        List<Long[]> synonymItemList = new ArrayList<Long[]>(sentence.size());
        for (Term term : sentence) {
            // 除掉停用词
            if (term.nature == null) continue;
            String nature = term.nature.toString();
            char firstChar = nature.charAt(0);
            switch (firstChar) {
                case 'm': {
                    if (!TextUtility.isAllChinese(term.word)) continue;
                }
                break;
                case 'w': {
                    continue;
                }
            }
            // 停用词
            if (CoreStopWordDictionary.contains(term.word)) continue;
            Long[] item = get(term.word);
            // logger.trace("{} {}", wordResult.word, Arrays.toString(item));
            if (item == null) {
                if (withUndefinedItem) {
                    item = new Long[]{Long.MAX_VALUE / 3};
                    synonymItemList.add(item);
                }

            } else {
                synonymItemList.add(item);
            }
        }

        return synonymItemList;
    }

    /**
     * 获取语义标签
     *
     * @return
     */
    public static long[] getLexemeArray(List<CommonSynonymDictionary.SynonymItem> synonymItemList) {
        long[] array = new long[synonymItemList.size()];
        int i = 0;
        for (CommonSynonymDictionary.SynonymItem item : synonymItemList) {
            array[i++] = item.entry.id;
        }
        return array;
    }


    public long distance(List<CommonSynonymDictionary.SynonymItem> synonymItemListA, List<CommonSynonymDictionary.SynonymItem> synonymItemListB) {
        return EditDistance.compute(synonymItemListA, synonymItemListB);
    }

    public long distance(long[] arrayA, long[] arrayB) {
        return EditDistance.compute(arrayA, arrayB);
    }

}
