package com.huaban.analysis.jieba;

import org.apache.commons.lang3.StringUtils;

public class SegToken {
    public Word word;

    public int startOffset;

    public int endOffset;


    public SegToken(Word word, int startOffset, int endOffset) {
        this.word = word;
        this.startOffset = startOffset;
        this.endOffset = endOffset;
    }

    @Override
    public String toString() {
        if (StringUtils.isBlank(this.word.getTokenType()))
            return "[" + this.word.getToken() + ", " + startOffset + ", " + endOffset + "]";
        else
            return "[" + this.word.getToken() + ", " + startOffset + ", " + endOffset + ", " + this.word.getTokenType() + "]";
    }

}
