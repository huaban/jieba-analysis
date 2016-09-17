package org.xm.xmnlp.hanlp;

import org.xm.xmnlp.hanlp.corpus.io.IIOAdapter;
import org.xm.xmnlp.hanlp.seg.Segment;
import org.xm.xmnlp.hanlp.seg.Viterbi.ViterbiSegment;

import java.util.List;
import java.util.logging.Level;

import static org.xm.xmnlp.hanlp.utility.Predefine.logger;

/**
 * @author xuming
 */
public class HanLP {
    public static final class Config{
        public static boolean ShowTermNature = true;
        public static boolean Normalization= false;
        public static IIOAdapter IOAdapter;
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
         * 地名词典转移矩阵路径
         */
        public static String PlaceDictionaryTrPath = "data/dictionary/place/ns.tr.txt";
        /**
         * 地名词典路径
         */
        public static String OrganizationDictionaryPath = "data/dictionary/organization/nt.txt";
        /**
         * 地名词典转移矩阵路径
         */
        public static String OrganizationDictionaryTrPath = "data/dictionary/organization/nt.tr.txt";
        /**
         * 繁简词典路径
         */
        public static String TraditionalChineseDictionaryPath = "data/dictionary/tc/TraditionalChinese.txt";
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
        public static String CharTablePath = "data/dictionary/other/CharTable.bin.yes";

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


        public static void enableDebug(){
            enableDebug(true);
        }
        public static void enableDebug(boolean enable){
            DEBUG = enable;
            if(DEBUG){
                logger.setLevel(Level.ALL);
            }else {
                logger.setLevel(Level.OFF);
            }
        }
        public static Segment newSegment(){
            return new ViterbiSegment();
        }
//        public static String converToSimplifiedChinese(String trandtionalChineseString){
//            return TraditioinalChineseDictionary.convertToSimplifiedChinese(trandtionalChineseString.toCharArray());
//        }
//        public static String converToTraditionalChinese(String simplifiedChineseString){
//            return SimplifiedChineseDictionary.convertToTraditionalChinese(simplifiedChineseString);
//        }
//        public static List<Pinyin> convertToPinyinList(String text){
//            return PinyinDictionary.convertToPinyin(text);
//        }
//
//        public static CoNLLSentence parseDependency(String sentence){
//            return NeuralNetworkDependencyParse.compute(sentence);
//        }
//        public static List<String> extractPhrase(String text,int size){
//            IPhraseExtractor extractor = new MutualInformationEntropyPhraseExtractor();
//            return extractor.extractPhrase(text,size);
//        }
//        public static List<String> extractKeyword (String document,int size){
//            return TextRankKeyword.getKeywordList(document,size);
//        }
//        public static List<String> extractSummary(String document,int size){
//            return TextRankSentence.getTopSentenceList(document,size);
//        }
//        public static String getSummary(String document,int maxLength){
//            return TextRankSentence.getSummary(document,maxLength);
//        }


    }

}
