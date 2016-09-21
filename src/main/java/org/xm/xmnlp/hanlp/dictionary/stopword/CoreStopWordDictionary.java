package org.xm.xmnlp.hanlp.dictionary.stopword;

import org.xm.xmnlp.hanlp.HanLP;
import org.xm.xmnlp.hanlp.corpus.io.ByteArray;
import org.xm.xmnlp.hanlp.corpus.io.IOUtil;
import org.xm.xmnlp.hanlp.seg.common.Term;
import org.xm.xmnlp.hanlp.utility.Predefine;
import org.xm.xmnlp.hanlp.utility.TextUtility;

import java.io.DataOutputStream;
import java.io.File;
import java.util.List;
import java.util.ListIterator;

/**
 * @author xuming
 */
public class CoreStopWordDictionary {
    static StopWordDictionary dictionary;

    static {
        ByteArray byteArray = ByteArray.createByteArray(HanLP.Config.CoreStopWordDictionaryPath + Predefine.BIN_EXT);
        if (byteArray == null) {
            try {
                dictionary = new StopWordDictionary(new File(HanLP.Config.CoreStopWordDictionaryPath));
                DataOutputStream out = new DataOutputStream(IOUtil.newOutputStream(HanLP.Config.CoreStopWordDictionaryPath + Predefine.BIN_EXT));
                dictionary.save(out);
                out.close();
            } catch (Exception e) {
                System.err.println("载入停用词词典" + HanLP.Config.CoreStopWordDictionaryPath + "失败" + TextUtility.exceptionToString(e));
            }
        } else {
            dictionary = new StopWordDictionary();
            dictionary.load(byteArray);
        }
    }

    public static boolean contains(String key) {
        return dictionary.contains(key);
    }

    public static Filter FILTER = new Filter() {
        @Override
        public boolean shouldInclude(Term term) {
            // 除掉停用词
            String nature = term.nature != null ? term.nature.toString() : "空";
            char firstChar = nature.charAt(0);
            switch (firstChar) {
                case 'm':
                case 'b':
                case 'c':
                case 'e':
                case 'o':
                case 'p':
                case 'q':
                case 'u':
                case 'y':
                case 'z':
                case 'r':
                case 'w': {
                    return false;
                }
                default: {
                    if (term.word.length() > 1 && !CoreStopWordDictionary.contains(term.word)) {
                        return true;
                    }
                }
                break;
            }

            return false;
        }
    };

    public static boolean shouldInclude(Term term) {
        return FILTER.shouldInclude(term);
    }

    public static boolean shouldRemove(Term term) {
        return !shouldInclude(term);
    }

    public static boolean add(String stopWord) {
        return dictionary.add(stopWord);
    }

    public static boolean remove(String stopWord) {
        return dictionary.remove(stopWord);
    }

    public static void apply(List<Term> termList) {
        ListIterator<Term> listIterator = termList.listIterator();
        while (listIterator.hasNext()) {
            if (shouldRemove(listIterator.next()))
                listIterator.remove();
        }
    }

}
