package org.xm.xmnlp.hanlp.demo;


import org.xm.xmnlp.hanlp.HanLP;
import org.xm.xmnlp.hanlp.seg.Segment;
import org.xm.xmnlp.hanlp.seg.common.Term;

import java.util.List;

/**
 * 机构名识别
 *
 * @author hankcs
 */
public class OrganizationRecognitionDemo {
    public static void main(String[] args) {
        String[] testCase = new String[]{
                "我在上海林原科技有限公司兼职工作，",
                "我经常在台川喜宴餐厅吃饭，",
                "偶尔去开元地中海影城看电影。另据《华尔街日报》报道",
        };
        Segment segment0 = HanLP.newSegment();
        for (String sentence : testCase) {
            List<Term> termList = segment0.seg(sentence);
            System.out.println(termList);
        }
        System.out.println("organization recognize result:");
        Segment segment = HanLP.newSegment().enableOrganizationRecognize(true);
        for (String sentence : testCase) {
            List<Term> termList = segment.seg(sentence);
            System.out.println(termList);
        }
    }
}
