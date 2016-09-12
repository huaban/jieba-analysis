package org.xm.xmnlp.word.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by xuming
 */
public class GenericTrie<V> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GenericTrie.class);


    private static final int INDEX_LENGTH=12000;
    private final TrieNode<V>[] ROOT_NODES_INDEX = new TrieNode[INDEX_LENGTH];
    public void clear(){
        for(int i = 0;i<INDEX_LENGTH;i++){
            ROOT_NODES_INDEX[i] = null;
        }
    }

    private static class TrieNode<V> implements Comparable{

        @Override
        public int compareTo(Object o) {
            return 0;
        }
    }
}
