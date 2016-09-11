package org.xm.xmnlp.jiebaseg.demo;

import org.xm.xmnlp.jiebaseg.Segmenter;
import org.xm.xmnlp.jiebaseg.Token;

import java.util.List;

/**
 * Created by mingzai on 2016/9/11.
 */
public class TokenDemo {

    public static void main(String[] args) {
        Segmenter segmenter = new Segmenter();
        String sentence = "他在普林思工作,金融有限公司。结婚的和尚未结婚的刘翔都很好。你知道他是刘禹锡吗？";
        List<Token> segList = segmenter.process(sentence);
        for (Token i : segList) {
            String word = i.word;
            System.out.println(word);
        }
        System.out.println("------all:");
        System.out.println(segmenter.process(sentence));
    }

}
