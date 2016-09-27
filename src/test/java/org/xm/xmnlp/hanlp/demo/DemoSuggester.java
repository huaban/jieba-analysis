package org.xm.xmnlp.hanlp.demo;

import org.xm.xmnlp.hanlp.suggest.Suggester;

/**
 * 文本推荐(句子级别，从一系列句子中挑出与输入句子最相似的那一个)
 * @author xuming
 */
public class DemoSuggester {
    public static void main(String[] args) {
        Suggester suggester = new Suggester();
        String[] titleArray=("威廉王子发表演说 呼吁保护野生动物\n"+
                "《时代》年度人物最终入围名单出炉 普京马云入选\n" +
                "“黑格比”横扫菲：菲吸取“海燕”经验及早疏散\n" +
                "日本保密法将正式生效 日媒指其损害国民知情权\n" +
                "英报告说空气污染带来“公共健康危机" ).split("\\n");
        for(String title: titleArray){
            suggester.addSentence(title);
        }

        System.out.println(suggester.suggest("陈述",2));
        System.out.println(suggester.suggest("kegebi"));
    }

}
