/**
 * 
 */
package com.huaban.analysis.jieba;

import com.huaban.analysis.jieba.JiebaSegmenter.SegMode;
import junit.framework.TestCase;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;


/**
 * @author matrix
 * 
 */
public class JiebaSegmenterExtendTest extends TestCase {
    private JiebaSegmenter segmenter = new JiebaSegmenter();
    String[] sentences =
            new String[] {
                          "订购弹性公网IP",
                          "订购IPSEC VPN"
                           };

    @Override
    protected void setUp() throws Exception {
        WordDictionary.getInstance().init(Paths.get("conf"), ",");
    }


    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }


    @Test
    public void testCutForSearch() {
        for (String sentence : sentences) {
            List<SegToken> tokens = segmenter.processExtend(sentence, SegMode.SEARCH);
            System.out.print(String.format(Locale.getDefault(), "\n%s\n%s", sentence, tokens.toString()));
        }
    }


    @Test
    public void testCutForIndex() {
        for (String sentence : sentences) {
            List<SegToken> tokens = segmenter.processExtend(sentence, SegMode.INDEX);
            System.out.print(String.format(Locale.getDefault(), "\n%s\n%s", sentence, tokens.toString()));
        }
    }
}
