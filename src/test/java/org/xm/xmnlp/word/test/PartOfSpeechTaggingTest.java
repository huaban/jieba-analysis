package org.xm.xmnlp.word.test;

import org.xm.xmnlp.word.WordSegmenter;
import org.xm.xmnlp.word.segmentation.Word;
import org.xm.xmnlp.word.tagging.PartOfSpeechTagging;

import java.util.List;

/**
 * Created by mingzai on 2016/9/13.
 */
public class PartOfSpeechTaggingTest {
    public static void main(String[] args){
        List<Word> words = WordSegmenter.seg("我爱你江山，我更爱美人。");
        System.out.println("未标注词性："+words);
        //词性标注
        PartOfSpeechTagging.process(words);
        System.out.println("标注词性："+words);
    }

}
