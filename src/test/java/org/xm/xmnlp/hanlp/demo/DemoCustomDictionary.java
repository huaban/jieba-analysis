/*
 * <summary></summary>
 * <author>He Han</author>
 * <email>hankcs.cn@gmail.com</email>
 * <create-date>2014/12/9 13:04</create-date>
 *
 * <copyright file="DemoCustomDictionary.java" company="上海林原信息科技有限公司">
 * Copyright (c) 2003-2014, 上海林原信息科技有限公司. All Right Reserved, http://www.linrunsoft.com/
 * This source is subject to the LinrunSpace License. Please contact 上海林原信息科技有限公司 to get more information.
 * </copyright>
 */
package org.xm.xmnlp.hanlp.demo;


import org.xm.xmnlp.hanlp.HanLP;
import org.xm.xmnlp.hanlp.collection.AhoCorasick.AhoCorasickDoubleArrayTrie;
import org.xm.xmnlp.hanlp.dictionary.BaseSearcher;
import org.xm.xmnlp.hanlp.dictionary.CoreDictionary;
import org.xm.xmnlp.hanlp.dictionary.CustomDictionary;

import java.util.Map;

/**
 * 演示用户词典的动态增删
 *
 * @author hankcs
 */
public class DemoCustomDictionary {
    public static void main(String[] args) {
        // 动态增加
        CustomDictionary.add("攻城狮");
        // 强行插入
        CustomDictionary.insert("白富美", "nz 1024");
        // 删除词语（注释掉试试）
//        CustomDictionary.remove("攻城狮");
        System.out.println(CustomDictionary.add("单身狗", "n 10000"));
        System.out.println(CustomDictionary.get("单身狗"));

        //String text = "攻城狮逆袭单身狗，迎娶白富美，走上人生巅峰,他家的单身狗很厉害，什么狗？";  // 怎么可能噗哈哈！
        String text = "攻城狮逆袭单身狗，迎娶白富美，走上人生巅峰，这是AceBeverage公司的中华人名共和国的新世纪。";

        // DoubleArrayTrie分词
        final char[] charArray = text.toCharArray();
        CustomDictionary.parseText(charArray, new AhoCorasickDoubleArrayTrie.IHit<CoreDictionary.Attribute>() {
            @Override
            public void hit(int begin, int end, CoreDictionary.Attribute value) {
                System.out.printf("[%d:%d]=%s %s\n", begin, end, new String(charArray, begin, end - begin), value);
            }
        });
        // 首字哈希之后二分的trie树分词
        BaseSearcher searcher = CustomDictionary.getSearcher(text);
        Map.Entry entry;
        while ((entry = searcher.next()) != null) {
            System.out.println(entry);
        }

        // 标准分词
        System.out.println(HanLP.segment(text));

        // Note:动态增删不会影响词典文件
        // 目前CustomDictionary使用DAT储存词典文件中的词语，用BinTrie储存动态加入的词语，前者性能高，后者性能低
        // 之所以保留动态增删功能，一方面是历史遗留特性，另一方面是调试用；未来可能会去掉动态增删特性。
    }
}
