package org.xm.xmnlp.word.segmentation.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xm.xmnlp.word.recognition.RecognitionTool;
import org.xm.xmnlp.word.segmentation.Segmentation;
import org.xm.xmnlp.word.segmentation.SegmentationAlgorithm;
import org.xm.xmnlp.word.segmentation.SegmentationFactory;
import org.xm.xmnlp.word.segmentation.Word;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;


/**
 * Created by xuming
 */
public class FullSegmentation extends AbstractSegmentation {
    private static final Logger LOGGER = LoggerFactory.getLogger(FullSegmentation.class);
    private static final AbstractSegmentation RMM = (AbstractSegmentation) SegmentationFactory.getSegmentation(SegmentationAlgorithm.ReverseMaximumMatching);
    private static final int PROCESS_TEXT_LENGTH_THAN = 50;
    private static final int CHAR_IS_WORD_LENGTH_LEES_THAN = 18;


    @Override
    public List<Word> segImpl(String text) {
        if (text.length() > PROCESS_TEXT_LENGTH_THAN) {
            return RMM.segImpl(text);
        }

        List<Word>[] array = fullSeg(text);
        Map<List<Word>, Float> words = ngram(array);
        List<Word> result = disambiguity(words);
        return result;
    }

    private List<Word> disambiguity(Map<List<Word>, Float> words) {
        List<Entry<List<Word>, Float>> entries = words.entrySet()
                .parallelStream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .collect(Collectors.toList());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("ngram score:");
            int i = 1;
            for (Entry<List<Word>, Float> entry : entries) {
                LOGGER.debug("\t" + (i++) + ", word num:" + entry.getKey().size() + "\t ngram score:" + entry.getValue() + "\t" + entry.getKey());
            }
        }
        float maxScore = entries.get(0).getValue();
        Iterator<Entry<List<Word>, Float>> iter = entries.iterator();
        while (iter.hasNext()) {
            Entry<List<Word>, Float> entry = iter.next();
            if (entry.getValue() < maxScore) {
                entry.getKey().clear();
                iter.remove();
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("only keep highest score:");
            int i = 2;
            for (Entry<List<Word>, Float> entry : entries) {
                LOGGER.debug("\t" + (i++) + ", word num:" + entry.getKey().size() + "\t ngram score:" + entry.getValue() + "\t" + entry.getKey());
            }
        }
        int minSize = Integer.MAX_VALUE;
        List<Word> minSizeList = null;
        iter = entries.iterator();
        while (iter.hasNext()) {
            Entry<List<Word>, Float> entry = iter.next();
            if (entry.getKey().size() < minSize) {
                minSize = entry.getKey().size();
                if (minSizeList != null) {
                    minSizeList.clear();
                }
                minSizeList = entry.getKey();
            } else {
                entry.getKey().clear();
                iter.remove();
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("max score:" + maxScore + ", disambigulity result:" + minSizeList + ",word num:" + minSize);
        }
        return minSizeList;
    }

    private List<Word>[] fullSeg(String text) {
        final int textLen = text.length();
        final List<String>[] sequence = new LinkedList[textLen];
        if (isParallelSeg()) {
            List<Integer> list = new ArrayList<>();
            for (int i = 0; i < textLen; i++) {
                list.add(i);
            }
            list.parallelStream()
                    .forEach(i -> sequence[i] = fullSeg(text, i));
        } else {
            for (int i = 0; i < textLen; i++) {
                sequence[i] = fullSeg(text, i);
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("full segmentation middle result:");
            int i = 0;
            for (List<String> list : sequence) {
                LOGGER.debug("\t" + (i++) + "," + list);
            }
        }
        List<Node> leaf = new LinkedList<>();
        for (String word : sequence[0]) {
            Node node = new Node(word);
            buildNode(node, sequence, word.length(), leaf);
        }
        for (int j = 0; j < sequence.length; j++) {
            sequence[j].clear();
            sequence[j] = null;
        }
        List<Word>[] res = toWords(leaf);
        leaf.clear();
        return res;
    }

    private List<String> fullSeg(final String text, final int start) {
        List<String> result = new LinkedList<>();
        final int textLen = text.length();
        int len = textLen - start;
        int interceptLength = getInterceptLength();
        if (len > interceptLength) {
            len = interceptLength;
        }
        while (len > 1) {
            if (getDictionary().contains(text, start, len) || RecognitionTool.recog(text, start, len)) {
                result.add(text.substring(start, start + len));
            }
            len--;
        }
        if (textLen <= CHAR_IS_WORD_LENGTH_LEES_THAN || result.isEmpty()) {
            result.add(text.substring(start, start + 1));
        }
        return result;
    }

    private void buildNode(Node parent, List<String>[] sequence, int from, List<Node> leaf) {
        if (from >= sequence.length) {
            leaf.add(parent);
            return;
        }
        for (String item : sequence[from]) {
            Node child = new Node(item, parent);
            buildNode(child, sequence, from + item.length(), leaf);
        }
    }

    private List<Word>[] toWords(List<Node> leaf) {
        List<Word>[] result = new ArrayList[leaf.size()];
        int i = 0;
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("full segmentation result:");
        }
        for (Node node : leaf) {
            result[i++] = toWords(node);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("\t" + i + ": " + result[i - 1]);
            }
        }
        return result;
    }

    private List<Word> toWords(Node node) {
        Stack<String> stack = new Stack<>();
        while (node != null) {
            stack.push(node.getText());
            node = node.getParent();
        }
        int len = stack.size();
        List<Word> list = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            list.add(new Word(stack.pop()));
        }
        return list;
    }

    @Override
    public SegmentationAlgorithm getSegmentationAlgorithm() {
        return SegmentationAlgorithm.FullSegmentation;
    }

    private static class Node {
        private String text;

        private Node parent;

        public Node(String text) {
            this.text = text;
        }

        public Node(String text, Node parent) {
            this.text = text;
            this.parent = parent;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public void setParent(Node parent) {
            this.parent = parent;
        }

        public Node getParent() {
            return parent;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static void main(String[] args) {
        Segmentation segmentation = new FullSegmentation();
        String text = "ji结婚的和尚未结婚的小李";
        System.out.println(segmentation.seg(text));
    }

}