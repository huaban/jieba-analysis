package org.xm.xmnlp.hanlp.utility;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * @author xuming
 */
public class Predefine {
    public static  String HANLP_PROPERTIES_PATH;
    public final static double MIN_PROBABILITY = 1e-10;
    public final static int CT_SENTENCE_BEGIN = 1;        //Sentence begin
    public final static int CT_SENTENCE_END = 4;          //Sentence ending
    public final static int CT_SINGLE = 5;                //SINGLE byte
    public final static int CT_DELIMITER = CT_SINGLE + 1; //delimiter
    public final static int CT_CHINESE = CT_SINGLE + 2;   //Chinese Char
    public final static int CT_LETTER = CT_SINGLE + 3;    //HanYu Pinyin
    public final static int CT_NUM = CT_SINGLE + 4;       //HanYu Pinyin
    public final static int CT_INDEX = CT_SINGLE + 5;     //HanYu Pinyin
    public final static int CT_OTHER = CT_SINGLE + 12;    //Other
    /**
     * 浮点数正则
     */
    public static final Pattern PATTERN_FLOAT_NUMBER = Pattern.compile("^(-?\\d+)(\\.\\d+)?$");

    public static final int MAX_SEGMENT_NUM = 10;
    public static final int MAX_FREQUENCY = 2514057;
    public static final double dTemp = (double)1/MAX_FREQUENCY +0.00001;
    public static final double dSmoothingpara = 0.1;
    /**
     * 地址 ns
     */
    public final static String TAG_PLACE = "未##地";
    /**
     * 句子的开始 begin
     */
    public final static String TAG_BIGIN = "始##始";
    /**
     * 其它
     */
    public final static String TAG_OTHER = "未##它";
    /**
     * 团体名词 nt
     */
    public final static String TAG_GROUP = "未##团";
    /**
     * 数词 m
     */
    public final static String TAG_NUMBER = "未##数";
    /**
     * 数量词 mq （现在觉得应该和数词同等处理，比如一个人和一人都是合理的）
     */
    public final static String TAG_QUANTIFIER = "未##量";
    /**
     * 专有名词 nx
     */
    public final static String TAG_PROPER = "未##专";
    /**
     * 时间 t
     */
    public final static String TAG_TIME = "未##时";
    /**
     * 字符串 x
     */
    public final static String TAG_CLUSTER = "未##串";
    /**
     * 结束 end
     */
    public final static String TAG_END = "末##末";
    /**
     * 人名 nr
     */
    public final static String TAG_PEOPLE = "未##人";

    public static Logger logger = Logger.getLogger("HanLP");
    static {
        logger.setLevel(Level.WARNING);
    }

    public final static String TRIE_EXT=".trie.dat";
    public final static String VALUE_EXT=".value.dat";
    public final static String REVERSE_EXT = ".reverse";
    public final static String BIN_EXT = ".bin";

}
