package org.xm.xmnlp.hanlp.demo;

import org.xm.xmnlp.hanlp.dictionary.CoreSynonymDictionary;

/**
 * @author xuming
 */
public class DemoRewriteText {
    public static void main(String[] args) {
        String text = "这个方法可以利用同义词词典将一段文本改写成意思相似的另一段文本";
        System.out.println(CoreSynonymDictionary.rewrite(text));

    }
}
