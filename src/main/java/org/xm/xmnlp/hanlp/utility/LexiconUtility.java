package org.xm.xmnlp.hanlp.utility;

import org.xm.xmnlp.hanlp.corpus.tag.Nature;
import org.xm.xmnlp.hanlp.corpus.util.CustomNatureUtility;
import org.xm.xmnlp.hanlp.dictionary.CoreDictionary;
import org.xm.xmnlp.hanlp.dictionary.CustomDictionary;

import java.util.Arrays;
import java.util.LinkedHashSet;

/**
 * @author xuming
 */
public class LexiconUtility {
    public static CoreDictionary.Attribute getAttribute(String word) {
        CoreDictionary.Attribute attribute = CoreDictionary.get(word);
        if (attribute != null) return attribute;
        return CustomDictionary.get(word);
    }

    public static Nature convertStringToNature(String name, LinkedHashSet<Nature> customNaureCollector) {
        try {
            return Nature.valueOf(name);
        } catch (Exception e) {
            Nature nature = CustomNatureUtility.addNature(name);
            if (customNaureCollector != null) customNaureCollector.add(nature);
            return nature;
        }
    }

    public static int getFrequency(String word) {
        CoreDictionary.Attribute attribute = getAttribute(word);
        if (attribute == null) return 0;
        return attribute.totalFrequency;
    }

    /**
     * 设置某个单词的属性
     *
     * @param word
     * @param attribute
     * @return
     */
    public static boolean setAttribute(String word, CoreDictionary.Attribute attribute) {
        if (attribute == null) return false;

        if (CoreDictionary.trie.set(word, attribute)) return true;
        if (CustomDictionary.dat.set(word, attribute)) return true;
        CustomDictionary.trie.put(word, attribute);
        return true;
    }

    /**
     * 设置某个单词的属性
     *
     * @param word
     * @param natures
     * @return
     */
    public static boolean setAttribute(String word, Nature... natures) {
        if (natures == null) return false;

        CoreDictionary.Attribute attribute = new CoreDictionary.Attribute(natures, new int[natures.length]);
        Arrays.fill(attribute.frequency, 1);

        return setAttribute(word, attribute);
    }

    /**
     * 设置某个单词的属性
     *
     * @param word
     * @param natures
     * @return
     */
    public static boolean setAttribute(String word, String... natures) {
        if (natures == null) return false;

        Nature[] natureArray = new Nature[natures.length];
        for (int i = 0; i < natureArray.length; i++) {
            natureArray[i] = Nature.create(natures[i]);
        }

        return setAttribute(word, natureArray);
    }


    /**
     * 设置某个单词的属性
     *
     * @param word
     * @param natureWithFrequency
     * @return
     */
    public static boolean setAttribute(String word, String natureWithFrequency) {
        CoreDictionary.Attribute attribute = CoreDictionary.Attribute.create(natureWithFrequency);
        return setAttribute(word, attribute);
    }
}
