package org.xm.xmnlp.word.dictionary.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xm.xmnlp.word.dictionary.Dictionary;
import org.xm.xmnlp.word.util.WordConfTools;

import java.util.*;

/**
 * Created by mingzai on 2016/9/11.
 */
public class DictionaryTrie implements Dictionary {
    private static final Logger LOGGER = LoggerFactory.getLogger(DictionaryTrie.class);
    private static final int INDEX_LENGTH = WordConfTools.getInt("dictionary.trie.index.size", 24000);
    private static final TrieNode[] ROOT_NODES_INDEX = new TrieNode[INDEX_LENGTH];
    private int maxLength;

    public DictionaryTrie() {
        LOGGER.info("init dictionary:" + this.getClass().getName());
    }

    @Override
    public int getMaxLength() {
        return maxLength;
    }

    @Override
    public boolean contains(String item) {
        return contains(item, 0, item.length());
    }

    @Override
    public boolean contains(String item, int start, int length) {
        if (start < 0 || length < 1) {
            return false;
        }
        if (item == null || item.length() < length) {
            return false;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("search dict:{}", item.substring(start, start + length));
        }
        TrieNode node = getRootNode(item.charAt(start));
        if (node == null) {
            return false;
        }
        for (int i = 1; i < length; i++) {
            char character = item.charAt(i + start);
            TrieNode child = node.getChild(character);
            if (child == null) {
                return false;
            } else {
                node = child;
            }
        }
        if (node.isTerminal()) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("word found:{}", item.substring(start, start + length));
            }
            return true;
        }
        return false;
    }

    @Override
    public void addAll(List<String> items) {
        for (String item : items) {
            add(item);
        }
    }

    @Override
    public void add(String item) {
        item = item.trim();
        int len = item.length();
        if (len < 1) {
            return;
        }
        if (len > maxLength) {
            maxLength = len;
        }
        TrieNode node = getRootNodeIfNotExist(item.charAt(0));
        for (int i = 1; i < len; i++) {
            char character = item.charAt(i);
            TrieNode child = node.getChildIfNotExist(character);
            node = child;
        }
        node.setTerminal(true);
    }

    private TrieNode getRootNodeIfNotExist(char c) {
        TrieNode trieNode = getRootNode(c);
        if (trieNode == null) {
            trieNode = new TrieNode(c);
            addRootNode(trieNode);
        }
        return trieNode;
    }

    private void addRootNode(TrieNode rootNode) {
        int index = rootNode.getCharacter() % INDEX_LENGTH;
        TrieNode existTrieNode = ROOT_NODES_INDEX[index];
        if (existTrieNode != null) {
            rootNode.setSibling(existTrieNode);
        }
        ROOT_NODES_INDEX[index] = rootNode;
    }

    @Override
    public void removeAll(List<String> items) {
        for (String item : items) {
            remove(item);
        }
    }

    @Override
    public void remove(String item) {
        if (item == null || item.isEmpty()) {
            return;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("delete from dict:{}", item);
        }
        TrieNode node = getRootNode(item.charAt(0));
        if (node == null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("word not exist:{}", item);
            }
            return;
        }
        int length = item.length();
        for (int i = 1; i < length; i++) {
            char character = item.charAt(i);
            TrieNode child = node.getChild(character);
            if (child == null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("word not exist:{}" + item);
                }
                return;
            } else {
                node = child;
            }
        }
        if (node.isTerminal()) {
            node.setTerminal(false);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("remove word:{}", item);
            }
        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("word not exist:{}", item);
            }
        }
    }

    private TrieNode getRootNode(char character) {
        int index = character % INDEX_LENGTH;
        TrieNode trieNode = ROOT_NODES_INDEX[index];
        while (trieNode != null && character != trieNode.getCharacter()) {
            trieNode = trieNode.getSibling();
        }
        return trieNode;

    }

    @Override
    public void clear() {
        for (int i = 0; i < INDEX_LENGTH; i++) {
            ROOT_NODES_INDEX[i] = null;
        }
    }

    private static class TrieNode implements Comparable {
        private char character;

        private boolean terminal;
        private TrieNode sibling;
        private TrieNode[] children = new TrieNode[0];

        public TrieNode(char character) {
            this.character = character;
        }

        public boolean isTerminal() {
            return terminal;
        }

        public void setTerminal(boolean terminal) {
            this.terminal = terminal;
        }

        public char getCharacter() {
            return character;
        }

        public void setCharacter(char character) {
            this.character = character;
        }

        public TrieNode getSibling() {
            return sibling;
        }

        public void setSibling(TrieNode sibling) {
            this.sibling = sibling;
        }

        public TrieNode[] getChildren() {
            return children;
        }

        public void setChildren(TrieNode[] children) {
            this.children = children;
        }

        @Override
        public int compareTo(Object o) {
            return this.getCharacter() - (char) o;
        }

        public TrieNode getChild(char character) {
            int index = Arrays.binarySearch(children, character);
            if (index >= 0) {
                return children[index];
            }
            return null;
        }

        public TrieNode getChildIfNotExist(char character) {
            TrieNode child = getChild(character);
            if (child == null) {
                child = new TrieNode(character);
                addChild(child);
            }
            return child;
        }

        public void addChild(TrieNode child) {
            children = insert(children, child);
        }

        private TrieNode[] insert(TrieNode[] array, TrieNode element) {
            int length = array.length;
            if (length == 0) {
                array = new TrieNode[1];
                array[0] = element;
                return array;
            }
            TrieNode[] newArray = new TrieNode[length + 1];
            boolean isInsert = false;
            for (int i = 0; i < length; i++) {
                if (element.getCharacter() <= array[i].getCharacter()) {
                    newArray[i] = element;
                    System.arraycopy(array, i, newArray, i + 1, length - i);
                    isInsert = true;
                    break;
                } else {
                    newArray[i] = array[i];
                }
            }
            if (!isInsert) {
                newArray[length] = element;
            }
            return newArray;
        }


    }

    public void show(char character) {
        show(getRootNode(character), "");
    }

    public void show() {
        for (TrieNode node : ROOT_NODES_INDEX) {
            if (node != null) {
                show(node, "");
            }
        }
    }

    private void show(TrieNode node, String indent) {
        if (node.isTerminal()) {
            LOGGER.info(indent + node.getCharacter() + "(T)");
        } else {
            LOGGER.info(indent + node.getCharacter());
        }
        for (TrieNode item : node.getChildren()) {
            show(item, indent + "\t");
        }
    }

    public void showConflict() {
        int emptySlot = 0;
        Map<Integer, Integer> map = new HashMap<>();
        for (TrieNode node : ROOT_NODES_INDEX) {
            if (node == null) {
                emptySlot++;
            } else {
                int i = 0;
                while ((node = node.getSibling()) != null) {
                    i++;
                }
                if (i > 0) {
                    Integer value = map.get(i);
                    if (value == null) {
                        value = 1;
                    } else {
                        value++;
                    }
                    map.put(i, value);
                }
            }
        }
        int count = 0;
        for (int key : map.keySet()) {
            int value = map.get(key);
            count += key * value;
            LOGGER.info("conflict times:" + key + " the count of element:" + value);
        }
        LOGGER.info("conflict times:" + count);
        LOGGER.info("total slot:" + INDEX_LENGTH);
        LOGGER.info("used slot num:" + (INDEX_LENGTH - emptySlot));
        LOGGER.info("usage rate:" + (float) (INDEX_LENGTH - emptySlot) / INDEX_LENGTH * 100 + "%");
        LOGGER.info("left solt num:" + emptySlot);
    }

    public List<String> prefix(String prefix) {
        List<String> result = new ArrayList<>();
        prefix = prefix.trim();
        int len = prefix.length();
        if (len < 1) {
            return result;
        }
        TrieNode node = getRootNode(prefix.charAt(0));
        if (node == null) {
            return result;
        }
        for (int i = 1; i < len; i++) {
            char character = prefix.charAt(i);
            TrieNode child = node.getChild(character);
            if (child == null) {
                return result;
            } else {
                node = child;
            }
        }
        for (TrieNode item : node.getChildren()) {
            result.add(prefix + item.getCharacter());
        }
        return result;
    }

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
        LOGGER.info(trie.prefix("中").toString());//with one more character
        LOGGER.info(trie.prefix("中华").toString());
        LOGGER.info(trie.prefix("A").toString());
        LOGGER.info(trie.prefix("中心").toString());
    }
}
