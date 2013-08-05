package com.huaban.analysis.jieba;

import java.util.HashMap;

public class TrieNode {
    public char key = (char)0;

    public HashMap<Character, TrieNode> childs = new HashMap<Character, TrieNode>();
    
    public TrieNode() {}
    
    public TrieNode(char key) {
        this.key = key;
    }
}
