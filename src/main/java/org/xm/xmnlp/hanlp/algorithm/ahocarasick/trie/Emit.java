package org.xm.xmnlp.hanlp.algorithm.ahocarasick.trie;


import org.xm.xmnlp.hanlp.algorithm.ahocarasick.interval.Interval;
import org.xm.xmnlp.hanlp.algorithm.ahocarasick.interval.Intervalable;

/**
 * 一个模式串匹配结果
 */
public class Emit extends Interval implements Intervalable
{
    /**
     * 匹配到的模式串
     */
    private final String keyword;

    /**
     * 构造一个模式串匹配结果
     * @param start 起点
     * @param end 重点
     * @param keyword 模式串
     */
    public Emit(final int start, final int end, final String keyword)
    {
        super(start, end);
        this.keyword = keyword;
    }

    /**
     * 获取对应的模式串
     * @return 模式串
     */
    public String getKeyword()
    {
        return this.keyword;
    }

    @Override
    public String toString()
    {
        return super.toString() + "=" + this.keyword;
    }
}
