package org.xm.xmnlp.hanlp.collection.trie;

import org.xm.xmnlp.hanlp.corpus.io.ByteArray;

import java.io.DataOutputStream;
import java.util.TreeMap;

/**
 * @author xuming
 */
public interface ITrie<V> {
    int build(TreeMap<String, V> keyValueMap);
    boolean save(DataOutputStream out);
    boolean load(ByteArray byteArray, V[] value);
    V get(char[] key);
    V get(String key);
    V[] getValueArray(V[] a);
    boolean containsKey(String key);
    int size();
}
