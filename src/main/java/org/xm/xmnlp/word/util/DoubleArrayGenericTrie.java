package org.xm.xmnlp.word.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by xuming
 */
public class DoubleArrayGenericTrie {
    private static final Logger LOGGER = LoggerFactory.getLogger(DoubleArrayGenericTrie.class);
    private int size = 65000;

    public DoubleArrayGenericTrie(int size) {
        this.size = size;
    }

    public DoubleArrayGenericTrie() {
        LOGGER.info("init double array trie:" + this.getClass().getName());
    }

    public void clear() {

    }

    private static class Node {
        private int code;
        private int depth;
        private int left;
        private int right;
        private int value;

        @Override
        public String toString() {
            return "Node{" +
                    "code=" + code + "[" + (char) code + "]" +
                    ", depth=" + depth +
                    ",left=" + left +
                    ",right=" + right +
                    ",value=" + value +
                    '}';
        }
    }

    private int[] check;
    private int[] base;
    private boolean[] used;
    private int nextCheckPos;

    private List<Node> toTree(Node parent, List<String> items, Map<String, Integer> map) {
        List<Node> siblings = new ArrayList<>();
        int prev = 0;
        for (int i = parent.left; i < parent.right; i++) {
            if (items.get(i).length() < parent.depth) {
                continue;
            }
            String item = items.get(i);
            int cur = 0;
            if(item.length() !=parent.depth){
                cur = (int)item.charAt(parent.depth);
            }
            if(cur!=prev || siblings.isEmpty()){
                Node node = new Node();
                node.depth = parent.depth +1;
                node.code = cur;
                node.left =i;
                if(cur == 0 || cur == item.charAt(item.length()-1)){
                    if(map.get(item)!=null){
                        node.value = map .get(item);
                    }
                }
                if(!siblings.isEmpty()){
                    siblings.get(siblings.size() -1).right = i;
                }
                siblings.add(node);
            }
            prev = cur;
        }
        if(!siblings.isEmpty()){
            siblings.get(siblings.size() -1).right = parent.right;
            if(LOGGER.isDebugEnabled()){
                if(items.size()<10){
                    LOGGER.debug("double array trie tree:");
                    siblings.forEach(s->LOGGER.debug(s.toString()));

                }
            }
        }
        return siblings;
    }

    private int toDoubleArray(List<Node> siblings,List<String> words,Map<String,Integer> map){
        int begin = 0;
        int index = (siblings.get(0).code > nextCheckPos)? siblings.get(0).code: nextCheckPos;
        boolean isFirst = true;
        while(true){
            index ++;
            if(check[index] !=0){
                continue;
            }else if(isFirst){
                nextCheckPos = index;
                isFirst = false;
            }

            begin = index -siblings.get(0).code;
            if(used[begin]){
                continue;
            }
            for(int i =1;i<siblings.size();i++){
                if(check[begin +siblings.get(i).code]!=0){
                    continue;
                }
            }
            break;
        }
        used[begin] = true;
        for(int i =0;i<siblings.size();i++){
            check[begin +siblings.get(i).code] = begin;
        }
        for(int i =0;i<siblings.size();i++){
            List<Node> newSiblings = toTree(siblings.get(i),words,map);
            if(newSiblings.isEmpty()){
                base[begin+siblings.get(i).code] = -1;
                check[begin +siblings.get(i).code] = siblings.get(i).value;
            }else {
                int h = toDoubleArray(newSiblings,words,map);
                base[begin+siblings.get(i).code] = h;
            }
        }
        return begin;
    }
    private  void allocate(int size){
        check = null;
        base = null;
        used = null;
        nextCheckPos = 0;
        base = new int[size];
        check = new int[size];
        used = new boolean[size];
        base[0] = 1;
    }
    private void init(List<String> items,Map<String,Integer> map){
        if(items == null || items.isEmpty()){
            return;
        }
        Node rootNode =new Node();
        rootNode.left=0;
        rootNode.right = items.size();
        rootNode.depth = 0;
        while (true){
            try{
                allocate(size);
                List<Node> siblings = toTree(rootNode,items,map);
                toDoubleArray(siblings,items,map);
                break;
            }catch (Exception e){
                size += size/10;
                LOGGER.error("space heap not enough,add to :"+size);
            }
        }
        items.clear();
        items =null;
        map.clear();
        map = null;
        used = null;
    }

    public int get(String item,int start,int length){
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("start search data:{}",item.substring(start,start+length));
        }
        if(base == null){
            return Integer.MIN_VALUE;
        }
        int lastChar = base[0];
        int index;
        for(int i = start;i<start+length;i++){
            index = lastChar+(int)item.charAt(i);
            if(index>=check.length|| index <0){
                return Integer.MIN_VALUE;
            }
            if(lastChar == check[index]){
                lastChar = base[index];
            }else{
                return Integer.MIN_VALUE;
            }
        }
        index = lastChar;
        if(index >=check.length || index<0){
            return Integer.MIN_VALUE;
        }
        if(base[index] < 0){
            if(LOGGER.isDebugEnabled()){
                LOGGER.debug("search the word in dict:{}"+item.substring(start,start+length));
            }
            return check[lastChar];
        }
        return Integer.MIN_VALUE;
    }
    public int get(String item){
        return get(item,0,item.length());
    }
    public void putAll(Map<String,Integer> map){
        if(check !=null){
            throw new RuntimeException("addAll method can just be used oncce after clear");
        }

    }

}
