package org.xm.xmnlp.hanlp;

import org.xm.xmnlp.hanlp.corpus.io.IIOAdapter;
import org.xm.xmnlp.hanlp.dictionary.py.Pinyin;
import org.xm.xmnlp.hanlp.dictionary.py.PinyinDictionary;
import org.xm.xmnlp.hanlp.seg.Segment;
import org.xm.xmnlp.hanlp.seg.Viterbi.ViterbiSegment;
import org.xm.xmnlp.hanlp.seg.common.Term;
import org.xm.xmnlp.hanlp.summary.TextRankKeyword;
import org.xm.xmnlp.hanlp.summary.TextRankSentence;
import org.xm.xmnlp.hanlp.tokenizer.StandardTokenizer;

import java.util.List;
import java.util.logging.Level;

import static org.xm.xmnlp.hanlp.utility.Predefine.logger;

/**
 * @author xuming
 */
public class HanLP {
    public static List<Term> segment(String text) {
        return StandardTokenizer.segment(text.toCharArray());
    }

    public static Segment newSegment() {
        return new ViterbiSegment();
    }

    public static final class Config {
        /**
         * 开发模式
         */
        public static boolean DEBUG = false;
        /**
         * 核心词典路径
         */
        public static String CoreDictionaryPath = "data/dictionary/CoreNatureDictionary.txt";
        /**
         * 核心词典词性转移矩阵路径
         */
        public static String CoreDictionaryTransformMatrixDictionaryPath = "data/dictionary/CoreNatureDictionary.tr.txt";
        /**
         * 用户自定义词典路径
         */
        public static String CustomDictionaryPath[] = new String[]{"data/dictionary/custom/CustomDictionary.txt"};
        /**
         * 2元语法词典路径
         */
        public static String BiGramDictionaryPath = "data/dictionary/CoreNatureDictionary.ngram.txt";

        /**
         * 停用词词典路径
         */
        public static String CoreStopWordDictionaryPath = "data/dictionary/stopwords.txt";
        /**
         * 同义词词典路径
         */
        public static String CoreSynonymDictionaryDictionaryPath = "data/dictionary/synonym/CoreSynonym.txt";
        /**
         * 人名词典路径
         */
        public static String PersonDictionaryPath = "data/dictionary/person/nr.txt";
        /**
         * 人名词典转移矩阵路径
         */
        public static String PersonDictionaryTrPath = "data/dictionary/person/nr.tr.txt";
        /**
         * 地名词典路径
         */
        public static String PlaceDictionaryPath = "data/dictionary/place/ns.txt";
        /**
         * 机构名词典转移矩阵路径
         */
        public static String PlaceDictionaryTrPath = "data/dictionary/place/ns.tr.txt";
        /**
         * 机构名词典路径
         */
        public static String OrganizationDictionaryPath = "data/dictionary/organization/nt.txt";
        /**
         * 地名词典转移矩阵路径
         */
        public static String OrganizationDictionaryTrPath = "data/dictionary/organization/nt.tr.txt";
        /**
         * 简繁转换词典根目录
         */
        public static String tcDictionaryRoot = "data/dictionary/tc/";
        /**
         * 声母韵母语调词典
         */
        public static String SYTDictionaryPath = "data/dictionary/pinyin/SYTDictionary.txt";

        /**
         * 拼音词典路径
         */
        public static String PinyinDictionaryPath = "data/dictionary/pinyin/pinyin.txt";

        /**
         * 音译人名词典
         */
        public static String TranslatedPersonDictionaryPath = "data/dictionary/person/nrf.txt";

        /**
         * 日本人名词典路径
         */
        public static String JapanesePersonDictionaryPath = "data/dictionary/person/nrj.txt";

        /**
         * 字符类型对应表
         */
        public static String CharTypePath = "data/dictionary/other/CharType.dat.yes";

        /**
         * 字符正规化表（全角转半角，繁体转简体）
         */
        public static String CharTablePath = "data/dictionary/other/CharTable.txt";

        /**
         * 词-词性-依存关系模型
         */
        public static String WordNatureModelPath = "data/model/dependency/WordNature.txt";

        /**
         * 最大熵-依存关系模型
         */
        public static String MaxEntModelPath = "data/model/dependency/MaxEntModel.txt";
        /**
         * 神经网络依存模型路径
         */
        public static String NNParserModelPath = "data/model/dependency/NNParserModel.txt";
        /**
         * CRF分词模型
         */
        public static String CRFSegmentModelPath = "data/model/segment/CRFSegmentModel.txt";
        /**
         * HMM分词模型
         */
        public static String HMMSegmentModelPath = "data/model/segment/HMMSegmentModel.bin";
        /**
         * CRF依存模型
         */
        public static String CRFDependencyModelPath = "data/model/dependency/CRFDependencyModelMini.txt";
        /**
         * 分词结果是否展示词性
         */
        public static boolean ShowTermNature = true;
        /**
         * 是否执行字符正规化（繁体->简体，全角->半角，大写->小写），切换配置后必须删CustomDictionary.txt.bin缓存
         */
        public static boolean Normalization = false;
        /**
         * IO适配器（默认null，表示从本地文件系统读取），实现com.hankcs.hanlp.corpus.io.IIOAdapter接口
         * 以在不同的平台（Hadoop、Redis等）上运行HanLP
         */
        public static IIOAdapter IOAdapter;

        public static void enableDebug() {
            enableDebug(true);
        }

        public static void enableDebug(boolean enable) {
            DEBUG = enable;
            if (DEBUG) {
                logger.setLevel(Level.ALL);
            } else {
                logger.setLevel(Level.OFF);
            }
        }

    }

    private HanLP() {
    }

//    public static String converToSimplifiedChinese(String trandtionalChineseString) {
//        return TraditioinalChineseDictionary.convertToSimplifiedChinese(trandtionalChineseString.toCharArray());
//    }
//
//    public static String converToTraditionalChinese(String simplifiedChineseString) {
//        return SimplifiedChineseDictionary.convertToTraditionalChinese(simplifiedChineseString);
//    }

    public static List<Pinyin> convertToPinyinList(String text) {
        return PinyinDictionary.convertToPinyin(text);
    }

    /**
     * 转化为拼音
     *
     * @param text       文本
     * @param separator  分隔符
     * @param remainNone 有些字没有拼音（如标点），是否保留它们的拼音（true用none表示，false用原字符表示）
     * @return 一个字符串，由[拼音][分隔符][拼音]构成
     */
    public static String convertToPinyinString(String text, String separator, boolean remainNone) {
        List<Pinyin> pinyinList = PinyinDictionary.convertToPinyin(text, true);
        int length = pinyinList.size();
        StringBuilder sb = new StringBuilder(length * (5 + separator.length()));
        int i = 1;
        for (Pinyin pinyin : pinyinList) {

            if (pinyin == Pinyin.none5 && !remainNone) {
                sb.append(text.charAt(i - 1));
            } else sb.append(pinyin.getPinyinWithoutTone());
            if (i < length) {
                sb.append(separator);
            }
            ++i;
        }
        return sb.toString();
    }

//    public static CoNLLSentence parseDependency(String sentence) {
//        return NeuralNetworkDependencyParse.compute(sentence);
//    }
//
//    public static List<String> extractPhrase(String text, int size) {
//        IPhraseExtractor extractor = new MutualInformationEntropyPhraseExtractor();
//        return extractor.extractPhrase(text, size);
//    }

    public static List<String> extractKeyword(String document, int size) {
        return TextRankKeyword.getKeywordList(document, size);
    }

    public static List<String> extractSummary(String document, int size) {
        return TextRankSentence.getTopSentenceList(document, size);
    }

    public static String getSummary(String document, int maxLength) {
        return TextRankSentence.getSummary(document, maxLength);
    }

}
