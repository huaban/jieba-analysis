package com.huaban.analysis.jieba;


/**
 * Created by WuLC on 2017/6/8.
 */
import junit.framework.TestCase;
import org.junit.Test;

public class TextRankTest extends TestCase
{
    @Test
    public void testTextRank()
    {
        TextRank t = new TextRank();
        String context = "TFIDF是很强的baseline，具有较强的普适性，如果没有太多经验的话，可以实现该算法基本能应付大部分关键词抽取的场景了。对于中文而言，中文分词和词性标注的性能对关键词抽取的效果至关重要。较复杂的算法各自有些问题，如Topic Model，它的主要问题是抽取的关键词一般过于宽泛，不能较好反映文章主题。这在我的博士论文中有专门实验和论述；TextRank实际应用效果并不比TFIDF有明显优势，而且由于涉及网络构建和随机游走的迭代算法，效率极低。这些复杂算法集中想要解决的问题，是如何利用更丰富的文档外部和内部信息进行抽取。如果有兴趣尝试更复杂的算法，我认为我们提出的基于SMT（统计机器翻译）的模型，可以较好地兼顾效率和效果";
        System.out.println(t.getKeyword("", context));
    }
}