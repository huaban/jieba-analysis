package org.xm.xmnlp.word.demo;

import org.xm.xmnlp.word.dictionary.impl.DictionaryTrie;

/**
 * Created by xuming
 */
public class DictionaryTrieDemo {
    public static void main(String[] args) {
        DictionaryTrie trie = new DictionaryTrie();
        trie.add("APP");
        trie.add("中华人民共和国");
        trie.add("中华人民打太极");
        trie.add("中华");
        trie.add("中心思想");
        trie.add("杨家将");
        trie.add("中央");
        trie.add("中华民国");
        trie.show();
        System.out.println(trie.prefix("中").toString());//with one more character
        System.out.println(trie.prefix("中华").toString());
        System.out.println(trie.prefix("A").toString());
        System.out.println(trie.prefix("中心").toString());
    }
}
