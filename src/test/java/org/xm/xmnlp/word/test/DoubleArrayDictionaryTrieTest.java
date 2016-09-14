package org.xm.xmnlp.word.test;

import org.xm.xmnlp.word.dictionary.impl.DoubleArrayDictionaryTrie;
import org.xm.xmnlp.word.util.DoubleArrayGenericTrie;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mingzai
 */
public class DoubleArrayDictionaryTrieTest {
    public static void main(String[] args) {
        testDoubleArrayGenericTrie();
        testDoubleArrayDictionaryTrie();
    }

    private static void testDoubleArrayDictionaryTrie() {
        DoubleArrayDictionaryTrie trie = new DoubleArrayDictionaryTrie();
        int max = trie.getMaxLength();
        System.out.println(max+"");
        System.out.println(trie.contains("中"));
        System.out.println(trie.contains("中华"));
        System.out.println(trie.contains("A"));
        System.out.println(trie.contains("中心"));
    }

    private static void testDoubleArrayGenericTrie(){
        DoubleArrayGenericTrie doubleArrayGenericTrie = new DoubleArrayGenericTrie();

        Map<String, Integer> map = new HashMap<>();
        map.put("章子怡", 101);
        map.put("刘亦菲", 99);
        map.put("刘", 11);
        map.put("刘诗诗", -1);
        map.put("巩俐", 1);
        map.put("中国", 2);
        map.put("主演", 3);

        //构造双数组前缀树
        doubleArrayGenericTrie.putAll(map);
        System.out.println("增加数据");

        System.out.println("查找 章子怡：" + doubleArrayGenericTrie.get("章子怡"));
        System.out.println("查找 刘："+doubleArrayGenericTrie.get("刘"));
        System.out.println("查找 刘亦菲：" + doubleArrayGenericTrie.get("刘亦菲"));
        System.out.println("查找 巩俐："+doubleArrayGenericTrie.get("巩俐"));
        System.out.println("查找 中国的巩俐是红高粱的主演 3 2：" + doubleArrayGenericTrie.get("中国的巩俐是红高粱的主演", 3, 2));
        System.out.println("查找 中国的巩俐是红高粱的主演 0 2：" + doubleArrayGenericTrie.get("中国的巩俐是红高粱的主演", 0, 2));
        System.out.println("查找 中国的巩俐是红高粱的主演 10 2：" + doubleArrayGenericTrie.get("中国的巩俐是红高粱的主演", 10, 2));
        System.out.println("查找 复仇者联盟2：" + doubleArrayGenericTrie.get("复仇者联盟2"));
        System.out.println("查找 白掌：" + doubleArrayGenericTrie.get("白掌"));
        System.out.println("查找 红掌：" + doubleArrayGenericTrie.get("红掌"));

        doubleArrayGenericTrie.clear();
        System.out.println("清除所有数据");

        System.out.println("查找 章子怡：" + doubleArrayGenericTrie.get("章子怡"));

        map.put("白掌", 1000);
        map.put("红掌", 1001);
        map.put("复仇者联盟2", -1000);

        doubleArrayGenericTrie.putAll(map);
        System.out.println("增加数据");

        System.out.println("查找 章子怡：" + doubleArrayGenericTrie.get("章子怡"));
        System.out.println("查找 复仇者联盟2："+doubleArrayGenericTrie.get("复仇者联盟2"));
        System.out.println("查找 白掌：" + doubleArrayGenericTrie.get("白掌"));
        System.out.println("查找 红掌："+doubleArrayGenericTrie.get("红掌"));
        System.out.println("查找 刘亦菲："+doubleArrayGenericTrie.get("刘亦菲"));
        System.out.println("查找 刘诗诗："+doubleArrayGenericTrie.get("刘诗诗"));
        System.out.println("查找 巩俐：" + doubleArrayGenericTrie.get("巩俐"));
    }
}
