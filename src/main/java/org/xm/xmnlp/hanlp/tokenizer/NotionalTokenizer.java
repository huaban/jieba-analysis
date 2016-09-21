package org.xm.xmnlp.hanlp.tokenizer;

import org.xm.xmnlp.hanlp.HanLP;
import org.xm.xmnlp.hanlp.dictionary.stopword.CoreStopWordDictionary;
import org.xm.xmnlp.hanlp.dictionary.stopword.Filter;
import org.xm.xmnlp.hanlp.seg.Segment;
import org.xm.xmnlp.hanlp.seg.common.Term;

import java.util.List;
import java.util.ListIterator;

/**
 * @author xuming
 */
public class NotionalTokenizer {
    /**
     * 预置分词器
     */
    static final Segment SEGMENT = HanLP.newSegment();

    public static List<Term> segment(String text) {
        return segment(text.toCharArray());
    }

    /**
     * 分词
     *
     * @param text 文本
     * @return 分词结果
     */
    public static List<Term> segment(char[] text) {
        List<Term> resultList = SEGMENT.seg(text);
        ListIterator<Term> listIterator = resultList.listIterator();
        while (listIterator.hasNext()) {
            if (!CoreStopWordDictionary.shouldInclude(listIterator.next())) {
                listIterator.remove();
            }
        }

        return resultList;
    }

    /**
     * 切分为句子形式
     *
     * @param text
     * @return
     */
    public static List<List<Term>> seg2sentence(String text) {
        List<List<Term>> sentenceList = SEGMENT.seg2sentence(text);
        for (List<Term> sentence : sentenceList) {
            ListIterator<Term> listIterator = sentence.listIterator();
            while (listIterator.hasNext()) {
                if (!CoreStopWordDictionary.shouldInclude(listIterator.next())) {
                    listIterator.remove();
                }
            }
        }

        return sentenceList;
    }

    /**
     * 切分为句子形式
     *
     * @param text
     * @param filterArrayChain 自定义过滤器链
     * @return
     */
    public static List<List<Term>> seg2sentence(String text, Filter... filterArrayChain) {
        List<List<Term>> sentenceList = SEGMENT.seg2sentence(text);
        for (List<Term> sentence : sentenceList) {
            ListIterator<Term> listIterator = sentence.listIterator();
            while (listIterator.hasNext()) {
                if (filterArrayChain != null) {
                    Term term = listIterator.next();
                    for (Filter filter : filterArrayChain) {
                        if (!filter.shouldInclude(term)) {
                            listIterator.remove();
                            break;
                        }
                    }
                }
            }
        }

        return sentenceList;
    }
}
