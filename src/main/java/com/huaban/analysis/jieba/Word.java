package com.huaban.analysis.jieba;

/**
 * Created by linkerlin on 3/21/14.
 */
public class Word implements CharSequence{
    private String token;
    private Double freq;
    private String tokenType;
    private static WordDictionary wordDict = WordDictionary.getInstance();

    private Word(String token, Double freq, String tokenType){
        this.token = token;
        this.freq = freq;
        this.tokenType = tokenType;
    }

    private Word(String token, Double freq){
        this.token = token;
        this.freq = freq;
        this.tokenType = "";
    }

    private Word(String token){
        this.token = token;
        this.freq = 0.0;
        this.tokenType = "";
    }

    public static Word createWord(String token, Double freq, String tokenType) {
        if(wordDict.containsWord(token))
            return wordDict.getWord(token);
        return new Word(token, freq, tokenType);
    }

    public static Word createWord(String token, Double freq) {
        if(wordDict.containsWord(token))
            return wordDict.getWord(token);
        return new Word(token, freq, "");
    }

    public static Word createWord(String token) {
        if(wordDict.containsWord(token))
            return wordDict.getWord(token);
        return new Word(token, 0.0, "");
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Double getFreq() {
        return freq;
    }

    public void setFreq(Double freq) {
        this.freq = freq;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    /**
     * Returns the length of this character sequence.  The length is the number
     * of 16-bit <code>char</code>s in the sequence.
     *
     * @return the number of <code>char</code>s in this sequence
     */
    @Override
    public int length() {
        return token.length();
    }

    /**
     * Returns the <code>char</code> value at the specified index.  An index ranges from zero
     * to <tt>length() - 1</tt>.  The first <code>char</code> value of the sequence is at
     * index zero, the next at index one, and so on, as for array
     * indexing.
     *
     * <p>If the <code>char</code> value specified by the index is a
     * <a href="{@docRoot}/java/lang/Character.html#unicode">surrogate</a>, the surrogate
     * value is returned.
     *
     * @param index the index of the <code>char</code> value to be returned
     * @return the specified <code>char</code> value
     * @throws IndexOutOfBoundsException if the <tt>index</tt> argument is negative or not less than
     *                                   <tt>length()</tt>
     */
    @Override
    public char charAt(int index) {
        return token.charAt(index);
    }

    /**
     * Returns a new <code>CharSequence</code> that is a subsequence of this sequence.
     * The subsequence starts with the <code>char</code> value at the specified index and
     * ends with the <code>char</code> value at index <tt>end - 1</tt>.  The length
     * (in <code>char</code>s) of the
     * returned sequence is <tt>end - start</tt>, so if <tt>start == end</tt>
     * then an empty sequence is returned.
     *
     * @param start the start index, inclusive
     * @param end   the end index, exclusive
     * @return the specified subsequence
     * @throws IndexOutOfBoundsException if <tt>start</tt> or <tt>end</tt> are negative,
     *                                   if <tt>end</tt> is greater than <tt>length()</tt>,
     *                                   or if <tt>start</tt> is greater than <tt>end</tt>
     */
    @Override
    public Word subSequence(int start, int end) {
        return createWord(token.subSequence(start, end).toString(),freq,tokenType);
    }
}
