package org.xm.xmnlp.word.segmentation;

/**
 * Created by mingzai on 2016/9/11.
 */
public enum SegmentationAlgorithm {

    MaximumMatching("正向最大匹配算法"),
    /**
     * 逆向最大匹配算法
     */
    ReverseMaximumMatching("逆向最大匹配算法"),
    /**
     * 正向最小匹配算法
     */
    MinimumMatching("正向最小匹配算法"),
    /**
     * 逆向最小匹配算法
     */
    ReverseMinimumMatching("逆向最小匹配算法"),
    /**
     * 双向最大匹配算法
     */
    BidirectionalMaximumMatching("双向最大匹配算法"),
    /**
     * 双向最小匹配算法
     */
    BidirectionalMinimumMatching("双向最小匹配算法"),
    /**
     * 双向最大最小匹配算法
     */
    BidirectionalMaximumMinimumMatching("双向最大最小匹配算法"),
    /**
     * 全切分算法
     */
    FullSegmentation("全切分算法"),

    /**
     * 最少词数算法
     */
    MinimalWordCount("最少词数算法"),

    /**
     * 最大Ngram分值算法
     */
    MaxNgramScore("最大Ngram分值算法"),

    /**
     * 针对纯英文文本的分词算法
     */
    PureEnglish("针对纯英文文本的分词算法");

    private SegmentationAlgorithm(String des){
        this.des = des;
    }
    private final String des;
    public String getDes() {
        return des;
    }
}
