package org.xm.xmnlp.jiebaseg;

/**
 * Created by mingzai on 2016/9/10.
 */
public class Token {

    public String word;
    public int startOffset;
    public int endOffset;
    public String nature;

    public Token(String word, int startOffset, int endOffset, String nature) {
        this.word = word;
        this.startOffset = startOffset;
        this.endOffset = endOffset;
        this.nature = nature;
    }

    @Override
    public String toString() {
        return  word + "/" + nature ;
    }
}
