package org.xm.xmnlp.hanlp.collection.trie.bintrie;

/**
 * @author xuming
 */
public class ValueArray<V> {

    V[] value;
    int offset;

    public ValueArray(V[] value) {
        this.value = value;
    }

    public V nextValue() {
        return value[offset++];
    }

    /**
     * 仅仅给子类用，不要用
     */
    protected ValueArray() {
    }

    public ValueArray setValue(V[] value) {
        this.value = value;
        return this;
    }
}
