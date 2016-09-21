package org.xm.xmnlp.hanlp.demo;

import org.xm.xmnlp.hanlp.HanLP;
import org.xm.xmnlp.hanlp.seg.NShort.NShortSegment;
import org.xm.xmnlp.hanlp.seg.Segment;

/**
 * @author xuming
 */
public class DemoNShortSegment {
    public static void main(String[] args) {
        HanLP.Config.enableDebug();
        Segment nShortSegment = new NShortSegment();
        String testCase = "结婚的和尚未结婚的丽丽很招人喜欢，蝴蝶今天早上打扮得漂漂亮亮去见刘老公公了。";
        System.out.println("NShort segment:"+nShortSegment.seg(testCase));
    }
}
