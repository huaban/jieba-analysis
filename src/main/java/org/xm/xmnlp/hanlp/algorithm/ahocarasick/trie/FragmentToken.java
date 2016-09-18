package org.xm.xmnlp.hanlp.algorithm.ahocarasick.trie;

public class FragmentToken extends Token
{

    public FragmentToken(String fragment)
    {
        super(fragment);
    }

    @Override
    public boolean isMatch()
    {
        return false;
    }

    @Override
    public Emit getEmit()
    {
        return null;
    }
}
