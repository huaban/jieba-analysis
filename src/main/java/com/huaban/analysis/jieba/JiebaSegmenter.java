package com.huaban.analysis.jieba;

import com.huaban.analysis.jieba.viterbi.FinalSeg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JiebaSegmenter {
    private static WordDictionary wordDict = WordDictionary.getInstance();
    private static FinalSeg finalSeg = FinalSeg.getInstance();

    public static enum SegMode {
        INDEX, SEARCH
    }

    private Map<Integer, List<Integer>> createDAG(String sentence) {
        Map<Integer, List<Integer>> dag = new HashMap<Integer, List<Integer>>();
        TrieNode trie = wordDict.getTrie();
        int N = sentence.length();
        int i = 0, j = 0;
        TrieNode p = trie;
        while (i < N) {
            char ch = sentence.charAt(j);
            if (p.childs.containsKey(ch)) {
                p = p.childs.get(ch);
                if (p.childs.containsKey(' ')) {
                    if (!dag.containsKey(i)) {
                        List<Integer> value = new ArrayList<Integer>();
                        dag.put(i, value);
                        value.add(j);
                    } else
                        dag.get(i).add(j);
                }
                j += 1;
                if (j >= N) {
                    i += 1;
                    j = i;
                    p = trie;
                }
            } else {
                p = trie;
                i += 1;
                j = i;
            }
        }
        for (i = 0; i < N; ++i) {
            if (!dag.containsKey(i)) {
                List<Integer> value = new ArrayList<Integer>();
                value.add(i);
                dag.put(i, value);
            }
        }
        return dag;
    }

    private Map<Integer, Pair<Integer>> calc(String sentence, Map<Integer, List<Integer>> dag) {
        int N = sentence.length();
        HashMap<Integer, Pair<Integer>> route = new HashMap<Integer, Pair<Integer>>();
        route.put(N, new Pair<Integer>(0, 0.0));
        for (int i = N - 1; i > -1; i--) {
            Pair<Integer> candidate = null;
            for (Integer x : dag.get(i)) {
                double freq =
                        wordDict.getFreq(sentence.substring(i, x + 1)) + route.get(x + 1).freq;
                if (null == candidate) {
                    candidate = new Pair<Integer>(x, freq);
                } else if (candidate.freq < freq) {
                    candidate.freq = freq;
                    candidate.key = x;
                }
            }
            route.put(i, candidate);
        }
        return route;
    }
 
    public List<SegToken> process(String paragraph, SegMode mode) {
        List<SegToken> tokens = new ArrayList<SegToken>();
        StringBuilder sb = new StringBuilder();
        int offset = 0;
        for (int i = 0; i < paragraph.length(); ++i) {
            char ch = CharacterUtil.regularize(paragraph.charAt(i));
            if (CharacterUtil.ccFind(ch))
                sb.append(ch);
            else {
                if (sb.length() > 0) {
                    // process
                    if (mode == SegMode.SEARCH) {
                        for (Word word : sentenceProcess(sb.toString())) {
                            tokens.add(new SegToken(word, offset, offset += word.length()));
                        }
                    } else {
                        for (Word token : sentenceProcess(sb.toString())) {
                            if (token.length() > 2) {
                                Word gram2;
                                int j = 0;
                                for (; j < token.length() - 1; ++j) {
                                    gram2 = token.subSequence(j, j + 2);
                                    if (wordDict.containsWord(gram2.getToken()))
                                        tokens.add(new SegToken(gram2, offset + j, offset + j + 2));
                                }
                            }
                            if (token.length() > 3) {
                                Word gram3;
                                int j = 0;
                                for (; j < token.length() - 2; ++j) {
                                    gram3 = token.subSequence(j, j + 3);
                                    if (wordDict.containsWord(gram3.getToken()))
                                        tokens.add(new SegToken(gram3, offset + j, offset + j + 3));
                                }
                            }
                            tokens.add(new SegToken(token, offset, offset += token.length()));
                        }
                    }
                    sb = new StringBuilder();
                    offset = i;
                }
                if (wordDict.containsWord(paragraph.substring(i, i + 1)))
                    tokens.add(new SegToken(wordDict.getWord(paragraph.substring(i, i + 1)), offset, ++offset));
                else
                    tokens.add(new SegToken(Word.createWord(paragraph.substring(i, i + 1)), offset, ++offset));
            }
        }
        if (sb.length() > 0)
            if (mode == SegMode.SEARCH) {
                for (Word token : sentenceProcess(sb.toString())) {
                    tokens.add(new SegToken(token, offset, offset += token.length()));
                }
            } else {
                for (Word token : sentenceProcess(sb.toString())) {
                    if (token.length() > 2) {
                        Word gram2;
                        int j = 0;
                        for (; j < token.length() - 1; ++j) {
                            gram2 = token.subSequence(j, j + 2);
                            if (wordDict.containsWord(gram2.getToken()))
                                tokens.add(new SegToken(gram2, offset + j, offset + j + 2));
                        }
                    }
                    if (token.length() > 3) {
                        Word gram3;
                        int j = 0;
                        for (; j < token.length() - 2; ++j) {
                            gram3 = token.subSequence(j, j + 3);
                            if (wordDict.containsWord(gram3.getToken()))
                                tokens.add(new SegToken(gram3, offset + j, offset + j + 3));
                        }
                    }
                    tokens.add(new SegToken(token, offset, offset += token.length()));
                }
            }

        return tokens;
    }

    public List<Word> sentenceProcess(String sentence) {
        List<Word> tokens = new ArrayList<Word>();
        int N = sentence.length();
        Map<Integer, List<Integer>> dag = createDAG(sentence);
        Map<Integer, Pair<Integer>> route = calc(sentence, dag);
        
        int x = 0;
        int y = 0;
        String buf = "";
        while (x < N) {
            y = route.get(x).key + 1;
            String lWord = sentence.substring(x, y);
            if (y - x == 1)
                buf += lWord;
            else {
                if (buf.length() > 0) {
                    if (buf.length() == 1) {
                        tokens.add(Word.createWord(buf));
                        buf = "";
                    } else {
                        if (wordDict.containsWord(buf)) {
                            tokens.add(wordDict.getWord(buf));
                        } else {
                            finalSeg.cut(buf, tokens);
                        }
                        buf = "";
                    }
                }
                tokens.add(Word.createWord(lWord));
            }
            x = y;
        }
        if (buf.length() > 0) {
            if (buf.length() == 1) {
                tokens.add(Word.createWord(buf));
                buf = "";
            } else {
                if (wordDict.containsWord(buf)) {
                    tokens.add(wordDict.getWord(buf));
                } else {
                    finalSeg.cut(buf, tokens);
                }
                buf = "";
            }

        }
        return tokens;
    }
}
