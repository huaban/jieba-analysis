package com.huaban.analysis.jieba;

public class SegToken {
    public String token;

    public int startOffset;

    public int endOffset;

    public SegToken(String token, int startOffset, int endOffset) {
	this.token = token;
	this.startOffset = startOffset;
	this.endOffset = endOffset;
    }

    @Override
    public String toString() {
	return "[" + token + ", " + startOffset + ", " + endOffset + "]";
    }

}
