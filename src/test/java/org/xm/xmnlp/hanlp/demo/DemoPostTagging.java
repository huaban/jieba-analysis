package org.xm.xmnlp.hanlp.demo;

import org.xm.xmnlp.hanlp.HanLP;
import org.xm.xmnlp.hanlp.seg.Segment;

/**
 * @author xuming
 */
public class DemoPostTagging {
    public static void main(String[] args){
        String text = "刘明教授正在教授自然语言处理课程及人文治理学 川岛芳子和村上春树很喜欢未名湖西餐厅";
        Segment segment = HanLP.newSegment();
        System.out.println("not posttagging:"+segment.seg(text));
        segment.enablePartOfSpeechTagging(true);
        System.out.println("after posttagging:"+segment.seg(text));
        segment.enableAllNamedEntityRecognize(true);
        System.out.println("enalbe all name:"+segment.seg(text));

    }
}
