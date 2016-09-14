package org.xm.xmnlp.word.test;

import org.xm.xmnlp.word.segmentation.Segmentation;
import org.xm.xmnlp.word.segmentation.SegmentationAlgorithm;
import org.xm.xmnlp.word.segmentation.Word;
import org.xm.xmnlp.word.segmentation.impl.AbstractSegmentation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mingzai
 */
public class AbstractSegmentationTest {
    public static void main(String[] args) {
        Segmentation englishSeg = new AbstractSegmentation() {
            @Override
            public SegmentationAlgorithm getSegmentationAlgorithm() {
                return SegmentationAlgorithm.FullSegmentation;
            }

            @Override
            public List<Word> segImpl(String text) {
                List<Word> words = new ArrayList<>();
                for(String word: text.split("\\s+")){
                    words.add(new Word(word));
                }
                return words;
            }
        };
        System.out.println(englishSeg.seg("i love programming "));
    }
}
