package org.xm.xmnlp.hanlp.seg.common;

import org.xm.xmnlp.hanlp.HanLP;
import org.xm.xmnlp.hanlp.corpus.tag.Nature;
import org.xm.xmnlp.hanlp.utility.LexiconUtility;

/**
 * @author xuming
 */
public class Term {
    /**
     * 词语
     */
    public String word;

    /**
     * 词性
     */
    public Nature nature;

    /**
     * 在文本中的起始位置（需开启分词器的offset选项）
     */
    public int offset;

    /**
     * 构造一个单词
     *
     * @param word   词语
     * @param nature 词性
     */
    public Term(String word, Nature nature) {
        this.word = word;
        this.nature = nature;
    }

    @Override
    public String toString() {
        if (HanLP.Config.ShowTermNature)
            return word + "/" + nature;
        return word;
    }

    /**
     * 长度
     *
     * @return
     */
    public int length() {
        return word.length();
    }

    /**
     * 获取本词语在HanLP词库中的频次
     *
     * @return 频次，0代表这是个OOV
     */
    public int getFrequency() {
        return LexiconUtility.getFrequency(word);
    }
}
