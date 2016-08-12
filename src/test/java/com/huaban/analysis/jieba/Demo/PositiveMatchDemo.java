package com.huaban.analysis.jieba.Demo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuming on 2016/5/27.
 */
public class PositiveMatchDemo {

    public static void main(String[] args){
        System.out.println("hi");

        String str = "我爱这个中华人民共和国大家庭";
        List<String> normalDict = new ArrayList<String>();

        normalDict.add("");
        normalDict.add("爱");
        normalDict.add("中华");   //测试词库里有中华和中华人民共和国，按照最大匹配应该匹配出中华人民共和国
        normalDict.add("中华人民共和国");

        int strLen = str.length();  //传入字符串的长度
        int j = 0;
        String matchWord = ""; //根据词库里识别出来的词
        int matchPos = 0; //根据词库里识别出来词后当前句子中的位置
        while (j < strLen) {      //从0字符匹配到字符串结束
            int matchPosTmp = 0;   //截取字符串的位置
            int i = 1;
            while (matchPosTmp < strLen) {   //从当前位置直到整句结束，匹配最大长度
                matchPosTmp = i + j;
                String keyTmp = str.substring(j, matchPosTmp);//切出最大字符串
                if (normalDict.contains(keyTmp)) { //判断当前字符串是否在词典中
                    matchWord = keyTmp;  //如果在词典中匹配上了就赋值
                    matchPos = matchPosTmp; //同时保存好匹配位置
                }
                i++;
            }
            if (!matchWord.isEmpty()) {
                //有匹配结果就输出最大长度匹配字符串
                j = matchPos;
                //保存位置，下次从当前位置继续往后截取
                System.out.print(matchWord + " ");
            } else {
                //从当前词开始往后都没有能够匹配上的词，则按照单字切分的原则切分
                System.out.print(str.substring(j, ++j) + " ");
            }
            matchWord = "";
        }

    }
}
