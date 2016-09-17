package org.xm.xmnlp.hanlp.collection.trie.bintrie;

/**
 * @author xuming
 */
public class EmptyValueArray<V> extends ValueArray<V> {
    public EmptyValueArray(){}
    @Override
    public V nextValue(){
        return null;
    }
}
