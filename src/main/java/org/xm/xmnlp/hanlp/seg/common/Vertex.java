package org.xm.xmnlp.hanlp.seg.common;

import org.xm.xmnlp.hanlp.corpus.tag.Nature;
import org.xm.xmnlp.hanlp.dictionary.CoreDictionary;
import org.xm.xmnlp.hanlp.utility.MathUtil;
import org.xm.xmnlp.hanlp.utility.Predefine;

import java.util.Map;

import static org.xm.xmnlp.hanlp.utility.Predefine.logger;

/**
 * @author xuming
 */
public class Vertex {
    public String word;
    public String realWord;
    public CoreDictionary.Attribute attribute;
    public int wordID;
    public int index;
    public Vertex from;
    public double weight;

    public void updateFrom(Vertex from) {
        double weight = from.weight + MathUtil.calculateWeight(from, this);
        if (this.from == null || this.weight > weight) {
            this.from = from;
            this.weight = weight;
        }
    }

    public Vertex(String word, String realWord, CoreDictionary.Attribute attribute) {
        this(word, realWord, attribute, -1);
    }

    public Vertex(String word, String realWord, CoreDictionary.Attribute attribute, int wordID) {
        if (attribute == null) {
            attribute = new CoreDictionary.Attribute(Nature.n, 1);
        }
        this.wordID = wordID;
        this.attribute = attribute;
        if (word == null) {
            word = compileRealWord(realWord, attribute);
        }
        assert realWord.length() > 0 : "构造空白节点会导致死循环！";
        this.word = word;
        this.realWord = realWord;

    }

    /**
     * 始##始
     */
    public static Vertex B = new Vertex(Predefine.TAG_BIGIN, " ", new CoreDictionary.Attribute(Nature.begin, Predefine.MAX_FREQUENCY / 10), CoreDictionary.getWordID(Predefine.TAG_BIGIN));
    /**
     * 末##末
     */
    public static Vertex E = new Vertex(Predefine.TAG_END, " ", new CoreDictionary.Attribute(Nature.begin, Predefine.MAX_FREQUENCY / 10), CoreDictionary.getWordID(Predefine.TAG_END));

    /**
     * 将原词转为等效词串
     *
     * @param realWord  原来的词
     * @param attribute 等效词串
     * @return
     */
    private String compileRealWord(String realWord, CoreDictionary.Attribute attribute) {
        if (attribute.nature.length == 1) {
            switch (attribute.nature[0]) {
                case nr:
                case nr1:
                case nr2:
                case nrf:
                case nrj: {
                    wordID = CoreDictionary.NR_WORD_ID;
//                    this.attribute = CoreDictionary.get(CoreDictionary.NR_WORD_ID);
                    return Predefine.TAG_PEOPLE;
                }
                case ns:
                case nsf: {
                    wordID = CoreDictionary.NS_WORD_ID;
                    // 在地名识别的时候,希望类似"河镇"的词语保持自己的词性,而不是未##地的词性
//                    this.attribute = CoreDictionary.get(CoreDictionary.NS_WORD_ID);
                    return Predefine.TAG_PLACE;
                }
//                case nz:
                case nx: {
                    wordID = CoreDictionary.NX_WORD_ID;
                    this.attribute = CoreDictionary.get(CoreDictionary.NX_WORD_ID);
                    return Predefine.TAG_PROPER;
                }
                case nt:
                case ntc:
                case ntcf:
                case ntcb:
                case ntch:
                case nto:
                case ntu:
                case nts:
                case nth:
                case nit: {
                    wordID = CoreDictionary.NT_WORD_ID;
                    this.attribute = CoreDictionary.get(CoreDictionary.NT_WORD_ID);
                    return Predefine.TAG_GROUP;
                }
                case m:
                case mq: {
                    wordID = CoreDictionary.M_WORD_ID;
                    this.attribute = CoreDictionary.get(CoreDictionary.M_WORD_ID);
                    return Predefine.TAG_NUMBER;
                }
                case x: {
                    wordID = CoreDictionary.X_WORD_ID;
                    this.attribute = CoreDictionary.get(CoreDictionary.X_WORD_ID);
                    return Predefine.TAG_CLUSTER;
                }
//                case xx:
//                case w:
//                {
//                    word= Predefine.TAG_OTHER;
//                }
//                break;
                case t: {
                    wordID = CoreDictionary.T_WORD_ID;
                    this.attribute = CoreDictionary.get(CoreDictionary.T_WORD_ID);
                    return Predefine.TAG_TIME;
                }
            }
        }

        return realWord;
    }

    /**
     * 真实词与编译词相同时候的构造函数
     *
     * @param realWord
     * @param attribute
     */
    public Vertex(String realWord, CoreDictionary.Attribute attribute) {
        this(null, realWord, attribute);
    }

    public Vertex(String realWord, CoreDictionary.Attribute attribute, int wordID) {
        this(null, realWord, attribute, wordID);
    }

    /**
     * 通过一个键值对方便地构造节点
     *
     * @param entry
     */
    public Vertex(Map.Entry<String, CoreDictionary.Attribute> entry) {
        this(entry.getKey(), entry.getValue());
    }

    /**
     * 自动构造一个合理的顶点
     *
     * @param realWord
     */
    public Vertex(String realWord) {
        this(null, realWord, CoreDictionary.get(realWord));
    }

    public Vertex(char realWord, CoreDictionary.Attribute attribute) {
        this(String.valueOf(realWord), attribute);
    }

    /**
     * 获取真实词
     *
     * @return
     */
    public String getRealWord() {
        return realWord;
    }

    /**
     * 获取词的属性
     *
     * @return
     */
    public CoreDictionary.Attribute getAttribute() {
        return attribute;
    }

    public boolean confirmNature(Nature nature) {
        if (attribute.nature.length == 1 && attribute.nature[0] == nature) {
            return true;
        }
        boolean result = true;
        int frequency = attribute.getNatureFrequency(nature);
        if (frequency == 0) {
            frequency = 1000;
            result = false;
        }
        attribute = new CoreDictionary.Attribute(nature, frequency);
        return result;
    }

    public boolean confirmNature(Nature nature, boolean updateWord) {
        switch (nature) {
            case m:
                word = Predefine.TAG_NUMBER;
                break;
            case t:
                word = Predefine.TAG_TIME;
                break;
            default:
                logger.warning("没有与" + nature + "对应的case");
                break;
        }
        return confirmNature(nature);
    }

    public Nature getNature() {
        if (attribute.nature.length == 1) {
            return attribute.nature[0];
        }
        return null;
    }

    /**
     * 猜测最可能的词性，也就是这个节点的词性中出现频率最大的那一个词性
     *
     * @return
     */
    public Nature guessNature() {
        return attribute.nature[0];
    }

    public boolean hasNature(Nature nature) {
        return attribute.getNatureFrequency(nature) > 0;
    }

    /**
     * 复制自己
     *
     * @return 自己的备份
     */
    public Vertex copy() {
        return new Vertex(word, realWord, attribute);
    }

    public Vertex setWord(String word) {
        this.word = word;
        return this;
    }

    public Vertex setRealWord(String realWord) {
        this.realWord = realWord;
        return this;
    }

    /**
     * 创建一个数词实例
     *
     * @param realWord 数字对应的真实字串
     * @return 数词顶点
     */
    public static Vertex newNumberInstance(String realWord) {
        return new Vertex(Predefine.TAG_NUMBER, realWord, new CoreDictionary.Attribute(Nature.m, 1000));
    }

    /**
     * 创建一个地名实例
     *
     * @param realWord 数字对应的真实字串
     * @return 地名顶点
     */
    public static Vertex newAddressInstance(String realWord) {
        return new Vertex(Predefine.TAG_PLACE, realWord, new CoreDictionary.Attribute(Nature.ns, 1000));
    }

    /**
     * 创建一个标点符号实例
     *
     * @param realWord 标点符号对应的真实字串
     * @return 标点符号顶点
     */
    public static Vertex newPunctuationInstance(String realWord) {
        return new Vertex(realWord, new CoreDictionary.Attribute(Nature.w, 1000));
    }

    /**
     * 创建一个人名实例
     *
     * @param realWord
     * @return
     */
    public static Vertex newPersonInstance(String realWord) {
        return newPersonInstance(realWord, 1000);
    }

    /**
     * 创建一个音译人名实例
     *
     * @param realWord
     * @return
     */
    public static Vertex newTranslatedPersonInstance(String realWord, int frequency) {
        return new Vertex(Predefine.TAG_PEOPLE, realWord, new CoreDictionary.Attribute(Nature.nrf, frequency));
    }

    /**
     * 创建一个日本人名实例
     *
     * @param realWord
     * @return
     */
    public static Vertex newJapanesePersonInstance(String realWord, int frequency) {
        return new Vertex(Predefine.TAG_PEOPLE, realWord, new CoreDictionary.Attribute(Nature.nrj, frequency));
    }

    /**
     * 创建一个人名实例
     *
     * @param realWord
     * @param frequency
     * @return
     */
    public static Vertex newPersonInstance(String realWord, int frequency) {
        return new Vertex(Predefine.TAG_PEOPLE, realWord, new CoreDictionary.Attribute(Nature.nr, frequency));
    }

    /**
     * 创建一个地名实例
     *
     * @param realWord
     * @param frequency
     * @return
     */
    public static Vertex newPlaceInstance(String realWord, int frequency) {
        return new Vertex(Predefine.TAG_PLACE, realWord, new CoreDictionary.Attribute(Nature.ns, frequency));
    }

    /**
     * 创建一个机构名实例
     *
     * @param realWord
     * @param frequency
     * @return
     */
    public static Vertex newOrganizationInstance(String realWord, int frequency) {
        return new Vertex(Predefine.TAG_GROUP, realWord, new CoreDictionary.Attribute(Nature.nt, frequency));
    }

    /**
     * 创建一个时间实例
     *
     * @param realWord 时间对应的真实字串
     * @return 时间顶点
     */
    public static Vertex newTimeInstance(String realWord) {
        return new Vertex(Predefine.TAG_TIME, realWord, new CoreDictionary.Attribute(Nature.t, 1000));
    }

    /**
     * 生成线程安全的起始节点
     *
     * @return
     */
    public static Vertex newB() {
        return new Vertex(Predefine.TAG_BIGIN, " ", new CoreDictionary.Attribute(Nature.begin, Predefine.MAX_FREQUENCY / 10), CoreDictionary.getWordID(Predefine.TAG_BIGIN));
    }

    /**
     * 生成线程安全的终止节点
     *
     * @return
     */
    public static Vertex newE() {
        return new Vertex(Predefine.TAG_END, " ", new CoreDictionary.Attribute(Nature.end, Predefine.MAX_FREQUENCY / 10), CoreDictionary.getWordID(Predefine.TAG_END));
    }

    @Override
    public String toString() {
        return realWord;
    }
}
