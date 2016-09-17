package org.xm.xmnlp.hanlp.dictionary;

import java.util.Map;

/**
 * @author xuming
 */
public abstract class BaseSearcher<V> {
    protected char[] c;
    protected int offset;
    protected BaseSearcher(char[] c){
        this.c = c;
    }
    protected BaseSearcher(String text){
        this(text.toCharArray());
    }
    public abstract Map.Entry<String,V> next();
    public int getOffset(){
        return offset;
    }
}
