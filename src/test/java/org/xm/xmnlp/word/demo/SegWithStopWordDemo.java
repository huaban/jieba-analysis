package org.xm.xmnlp.word.demo;

import org.xm.xmnlp.word.segmentation.Word;

import java.util.ArrayList;
import java.util.List;

import static org.xm.xmnlp.word.WordSegmenter.segWithoutStopWord;

/**
 * Created by mingzai
 */
public class SegWithStopWordDemo {
    public static void main(String[] args){
        List<String> sentences = new ArrayList<>();
        sentences.add("他说的确实在理");
        sentences.add("提高人民生活水平");
        sentences.add("他俩儿谈恋爱是从头年元月开始的");
        sentences.add("王府饭店的设施和服务是一流的");
        sentences.add("和服务于三日后裁制完毕，并呈送将军府中");
        sentences.add("研究生命的起源");
        sentences.add("他明天起身去北京");
        sentences.add("在这些企业中国有企业有十个");
        sentences.add("他站起身来");
        sentences.add("他们是来查金泰撞人那件事的");
        
        int j = 1;
        for (String sentence : sentences) {
            List<Word> words = segWithoutStopWord(sentence);
            System.out.println((j++) + "、切分句子: " + sentence);
            System.out.println("    切分结果：" + words);
        }
    }
}
