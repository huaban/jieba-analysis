package org.xm.xmnlp.word.test;

import org.xm.xmnlp.word.segmentation.Segmentation;
import org.xm.xmnlp.word.segmentation.impl.FullSegmentation;

/**
 * Created by xuming
 */
public class FullSegmentationTest {
    public static void main(String[] args) {
        Segmentation segmentation = new FullSegmentation();
        String text = "ji结婚的和尚未结婚的小李";
        System.out.println(segmentation.seg(text));
    }
}
