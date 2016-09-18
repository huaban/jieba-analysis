package org.xm.xmnlp.hanlp.demo;

import org.xm.xmnlp.hanlp.HanLP;
import org.xm.xmnlp.hanlp.seg.common.Term;
import org.xm.xmnlp.hanlp.tokenizer.StandardTokenizer;

import java.util.List;

import static java.awt.SystemColor.text;

/**
 * @author xuming
 */
public class StandardSegmentDemo {
    public static void main(String[] args) {
        HanLP.Config.enableDebug();
        String text = "举办纪念活动铭记二战历史，不忘战争带给人类的深重灾难，是为了防止悲剧重演，确保和平永驻；" +
                "铭记二战历史，更是为了提醒国际社会，需要共同捍卫二战胜利成果和国际公平正义，" +
                "必须警惕和抵制在历史认知和维护战后国际秩序问题上的倒行逆施。";
        System.out.println(StandardTokenizer.segment(text));
        // 测试分词速度，让大家对HanLP的性能有一个直观的认识
        List<Term> list = HanLP.segment("中华人名共和国大妈居委会");
        System.out.println(list);
    }
}
