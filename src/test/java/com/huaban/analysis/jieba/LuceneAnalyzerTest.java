package com.huaban.analysis.jieba;

import com.huaban.analysis.jieba.lucene.JiebaAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.Before;
import org.junit.Test;

import java.io.StringReader;
import java.nio.file.Paths;
import java.util.List;


/**
 * <pre>
 *
 * Created by zhenqin.
 * User: zhenqin
 * Date: 17/3/21
 * Time: 19:04
 * Vendor: NowledgeData
 * To change this template use File | Settings | File Templates.
 *
 * </pre>
 *
 * @author zhenqin
 */
public class LuceneAnalyzerTest {

    Analyzer analyzer = new JiebaAnalyzer(JiebaSegmenter.SegMode.INDEX.name());


    @Before
    public void setUp() throws Exception {
        WordDictionary.getInstance().loadUserDict(new StringReader("亲口交代 6 \n都要 10\n24口 10"));
        //WordDictionary.getInstance().addUserDictDir(Paths.get("/Volumes/Study/Work/github/jieba-analysis/conf"));
        //analyzer = new StandardAnalyzer(Version.LUCENE_46);
    }


    @Test
    public void testHello() throws Exception {
        JiebaSegmenter jiebaTagger = new JiebaSegmenter();
        List<SegToken> segTokens = jiebaTagger.process("工信处女干事每月经过下属科室都要亲口交代24口交换机等技术性器件的安装工作,结过婚的和尚未结过婚的恒大地产",
                JiebaSegmenter.SegMode.INDEX);
        for (SegToken segToken : segTokens) {
            System.out.println(segToken.word);
        }

    }

    @Test
    public void testWord() throws Exception {
        double i = WordDictionary.getInstance().getFreq("大数据");
        System.out.println(i);
    }

    @Test
    public void testPse1() throws Exception {
        String str = "工信处女干事每月经过下属科室都要亲口交代24口交换机等技术性器件的安装工作,结过婚的和尚未结过婚的恒大地产";
        TokenStream ts = analyzer.tokenStream("text", new StringReader(str));
        CharTermAttribute ch = ts.addAttribute(CharTermAttribute.class);
        ts.reset();
        while (ts.incrementToken()) {
            System.out.println(ch.toString());
        }
        ts.end();
        ts.close();
    }
}
