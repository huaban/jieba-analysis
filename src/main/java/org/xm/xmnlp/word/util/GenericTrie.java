package org.xm.xmnlp.word.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by xuming
 */
public class GenericTrie<V> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GenericTrie.class);
    private static final int INDEX_LENGTH = 12000;
    private final TrieNode<V>[] ROOT_NODES_INDEX = new TrieNode[INDEX_LENGTH];

    public void clear() {
        for (int i = 0; i < INDEX_LENGTH; i++) {
            ROOT_NODES_INDEX[i] = null;
        }
    }

    private void addRootNode(TrieNode<V> rootNode) {
        int index = rootNode.getCharacter() % INDEX_LENGTH;
        TrieNode<V> existTrieNode = ROOT_NODES_INDEX[index];
        if (existTrieNode != null) {
            rootNode.setSibling(existTrieNode);
        }
        ROOT_NODES_INDEX[index] = rootNode;
    }

    private TrieNode<V> getRootNode(char character) {
        int index = character % INDEX_LENGTH;
        TrieNode<V> trieNode = ROOT_NODES_INDEX[index];
        while (trieNode != null && character != trieNode.getCharacter()) {
            trieNode = trieNode.getSibling();
        }
        return trieNode;
    }


    private TrieNode<V> getRootNodeIfNotExistThenCreate(char character) {
        TrieNode<V> trieNode = getRootNode(character);
        if (trieNode == null) {
            trieNode = new TrieNode(character);
            addRootNode(trieNode);
        }
        return trieNode;
    }

    public V get(String item) {
        return get(item, 0, item.length());
    }

    private V get(String item, int start, int length) {
        if (start < 0 || length < 1) {
            return null;
        }
        if (item == null || item.length() < length) {
            return null;
        }
        TrieNode<V> node = getRootNode(item.charAt(start));
        if (node == null) {
            return null;
        }
        for (int i = 1; i < length; i++) {
            char character = item.charAt(i + start);
            TrieNode<V> child = node.getChild(character);
            if (child == null) {
                return null;
            } else {
                node = child;
            }
        }
        if (node.isTerminal()) {
            return node.getValue();
        }
        return null;
    }

    public void remove(String item) {
        if (item == null || item.isEmpty()) {
            return;
        }
        TrieNode<V> node = getRootNode(item.charAt(0));
        if (node == null) {
            return;
        }
        int length = item.length();
        for (int i = 1; i < length; i++) {
            char character = item.charAt(i);
            TrieNode<V> child = node.getChild(character);
            if (child == null) {
                return;
            } else {
                node = child;
            }
        }
        if (node.isTerminal()) {
            node.setTerminal(false);
            node.setValue(null);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("成功移除词性：" + item);
            }
        } else {
            LOGGER.debug("word not exists:" + item);
        }
    }

    public void put(String item, V value) {
        item = item.trim();
        int len = item.length();
        if (len < 1) {
            return;
        }
        TrieNode<V> node = getRootNodeIfNotExistThenCreate(item.charAt(0));
        for (int i = 1; i < len; i++) {
            char character = item.charAt(i);
            TrieNode<V> child = node.getChildIfNotExistThenCreate(character);
            node = child;
        }
        node.setTerminal(true);
        node.setValue(value);
    }

    private static class TrieNode<V> implements Comparable {
        private char character;
        private V value;
        private boolean terminal;
        private TrieNode<V> sibling;
        private TrieNode<V>[] children = new TrieNode[0];

        public TrieNode(char character) {
            this.character = character;
        }

        public char getCharacter() {
            return character;
        }

        public void setCharacter(char character) {
            this.character = character;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }

        public boolean isTerminal() {
            return terminal;
        }

        public void setTerminal(boolean terminal) {
            this.terminal = terminal;
        }

        public TrieNode<V> getSibling() {
            return sibling;
        }

        public void setSibling(TrieNode<V> sibling) {
            this.sibling = sibling;
        }

        public void setChildren(TrieNode<V>[] children) {
            this.children = children;
        }

        public Collection<TrieNode<V>> getChildren() {
            return Arrays.asList(children);
        }

        public TrieNode<V> getChild(char c) {
            int index = Arrays.binarySearch(children, c);
            if (index >= 0) {
                return children[index];
            }
            return null;
        }

        public TrieNode<V> getChildIfNotExistThenCreate(char c) {
            TrieNode<V> child = getChild(c);
            if (child == null) {
                child = new TrieNode(c);
                addChild(child);
            }
            return child;
        }

        public void addChild(TrieNode<V> child) {
            children = insert(children, child);
        }

        private TrieNode<V>[] insert(TrieNode<V>[] array, TrieNode<V> element) {
            int length = array.length;
            if (length == 0) {
                array = new TrieNode[1];
                array[0] = element;
                return array;
            }
            TrieNode<V>[] newArray = new TrieNode[length + 1];
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


        @Override
        public int compareTo(Object o) {
            return this.getCharacter() - (char) o;
        }
    }

    public void show(char c) {
        show(getRootNode(c), "");
    }

    public void show() {
        for (TrieNode<V> node : ROOT_NODES_INDEX) {
            if (node != null) {
                show(node, "");
            }
        }
    }

    public void show(TrieNode<V> node, String indent) {
        if (node.isTerminal()) {
            LOGGER.info(indent + node.getCharacter() + "=" + node.getValue() + "(T)");
        } else {
            LOGGER.info(indent + node.getCharacter());
        }
        for (TrieNode<V> item : node.getChildren()) {
            show(item, indent + "\t");
        }
    }

}
