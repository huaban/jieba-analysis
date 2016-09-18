package org.xm.xmnlp.hanlp.algorithm.ahocarasick.trie;

/**
 * 匹配到的片段
 */
public class MatchToken extends Token
{

    private Emit emit;

    public MatchToken(String fragment, Emit emit)
    {
        super(fragment);
        this.emit = emit;
    }

    @Override
    public boolean isMatch()
    {
        return true;
    }

    @Override
    public Emit getEmit()
    {
        return this.emit;
    }

}
