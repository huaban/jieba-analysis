package org.xm.xmnlp.word.segmentation.impl;

import org.xm.xmnlp.word.corpus.Bigram;
import org.xm.xmnlp.word.recognition.RecognitionTool;
import org.xm.xmnlp.word.segmentation.Segmentation;
import org.xm.xmnlp.word.segmentation.SegmentationAlgorithm;
import org.xm.xmnlp.word.segmentation.Word;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;


/**
 * Created by xuming
 */
public class MaxNgramScore extends AbstractSegmentation {

    @Override
    public SegmentationAlgorithm getSegmentationAlgorithm() {
        return SegmentationAlgorithm.MaxNgramScore;
    }

    @Override
    public List<Word> segImpl(String text) {
        final int textLen = text.length();
        Node start = new Node("S", 0);
        start.score = 0F;
        Node end = new Node("END", textLen + 1);
        Node[][] dag = new Node[textLen + 2][0];
        dag[0] = new Node[]{start};
        dag[textLen + 1] = new Node[]{end};
        if (isParallelSeg()) {
            List<Integer> list = new ArrayList<>(textLen);
            for (int i = 0; i < textLen; i++) {
                list.add(i);
            }
            list.parallelStream().forEach(i -> dag[i + 1] = fullSeg(text, i));
        } else {
            for (int i = 0; i < textLen; i++) {
                dag[i + 1] = fullSeg(text, i);
            }
        }
        dumpDAG(dag);
        boolean hasNGramScore = false;
        int following = 0;
        Node node = null;
        for (int i = 0; i < dag.length - 1; i++) {
            for (int j = 0; j < dag[i].length; j++) {
                node = dag[i][j];
                following = node.getFollowing();
                for (int k = 0; k < dag[following].length; k++) {
                    boolean result = dag[following][k].setPrevious(node);
                    if (result) {
                        hasNGramScore = true;
                    }
                }
            }
        }
        if (!hasNGramScore) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("所有切分结果都没有ngram分值，算法退化为 最少词数算法");
            }
            for (int i = 0; i < dag.length; i++) {
                for (int j = 0; j < dag[i].length; j++) {
                    dag[i][j].setScore(null);
                }
            }
            start.score = 1F;
            for (int i = 0; i < dag.length - 1; i++) {
                for (int j = 0; j < dag[i].length; j++) {
                    node = dag[i][j];
                    following = node.getFollowing();
                    for (int k = 0; k < dag[following].length; k++) {
                        dag[following][k].setPrevious(node, 1);
                    }
                }
            }
        }
        dumpShortestPath(dag);
        return toWords(end);
    }

    private static class Node {
        private String text;
        private Node previous;
        private int offset;
        private Float score;

        public Node(String text, int offset) {
            this.text = text;
            this.offset = offset;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public Node getPrevious() {
            return previous;
        }

        public boolean setPrevious(Node previous) {
            float score = Bigram.getScore(previous.getText(), this.getText());
            if (this.score == null) {
                this.score = previous.score + score;
                this.previous = previous;
            } else if (previous.score + score > this.score) {
                this.score = previous.score + score;
                this.previous = previous;
            }
            if (score > 0) {
                return true;
            }
            return false;
        }


        public int getOffset() {
            return offset;
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }

        public Float getScore() {
            return score;
        }

        public void setScore(Float score) {
            this.score = score;
        }

        public void setPrevious(Node previous, int distance) {
            if (this.score == null) {
                this.score = previous.score + distance;
                this.previous = previous;
            } else if (previous.score + distance < this.score) {
                this.score = previous.score + distance;
                this.previous = previous;
            }
        }

        public int getFollowing() {
            return this.offset + text.length();
        }

        @Override
        public String toString() {
            return "Node{" +
                    "text='" + text + '\'' +
                    ", previous=" + previous +
                    ", offset=" + offset +
                    ", score=" + score +
                    '}';
        }
    }

    private List<Word> toWords(Node node) {
        Stack<String> stack = new Stack<>();
        while ((node = node.getPrevious()) != null) {
            if (!"S".equals(node.getText())) {
                stack.push(node.getText());
            }
        }
        int len = stack.size();
        List<Word> list = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            list.add(new Word(stack.pop()));
        }
        return list;
    }

    private Node[] fullSeg(final String text, final int start) {
        List<Node> result = new LinkedList<>();
        result.add(new Node(text.substring(start, start + 1), start + 1));
        final int textLen = text.length();
        int len = textLen - start;
        int interceptLength = getInterceptLength();
        if (len > interceptLength) {
            len = interceptLength;
        }
        while (len > 1) {
            if (getDictionary().contains(text, start, len) || RecognitionTool.recog(text, start, len)) {
                result.add(new Node(text.substring(start, start + len), start + 1));
            }
            len--;
        }
        return result.toArray(new Node[0]);
    }

    private void dumpDAG(Node[][] dag) {
        if (LOGGER.isDebugEnabled()) {
            for (int i = 0; i < dag.length - 1; i++) {
                Node[] nodes = dag[i];
                StringBuilder line = new StringBuilder();
                for (Node node : nodes) {
                    int following = node.getFollowing();
                    StringBuilder followingNodeTexts = new StringBuilder();
                    for (int k = 0; k < dag[following].length; k++) {
                        String followingNodeText = dag[following][k].getText();
                        followingNodeTexts.append("(").append(followingNodeText).append(")");
                    }
                    line.append("[")
                            .append(node.getText())
                            .append("->").append(followingNodeTexts.toString())
                            .append("]\t");
                }
                LOGGER.debug(line.toString());
            }
        }
    }

    private void dumpShortestPath(Node[][] dag) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("有向无环图的最佳路径：");
            for (Node[] nodes : dag) {
                StringBuilder line = new StringBuilder();
                for (Node node : nodes) {
                    line.append("【")
                            .append(node.getText())
                            .append("(").append(node.getScore()).append(")")
                            .append("<-").append(node.getPrevious() == null ? "" : node.getPrevious().getText())
                            .append("】\t");
                }
                LOGGER.debug(line.toString());
            }
        }
    }


    public static void main(String[] args) {
        Segmentation segmentation = new MaxNgramScore();
        System.out.println(segmentation.seg("独立自主和平等互利的原则"));
    }
}
