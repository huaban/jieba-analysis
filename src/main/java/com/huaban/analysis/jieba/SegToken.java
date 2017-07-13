package com.huaban.analysis.jieba;

public class SegToken {
    public final String word;

    public final int startOffset;

    public final int endOffset;


    public SegToken(String word, int startOffset, int endOffset) {
        this.word = word;
        this.startOffset = startOffset;
        this.endOffset = endOffset;
    }


    @Override
    public String toString() {
        return "[" + word + ", " + startOffset + ", " + endOffset + "]";
    }

}
