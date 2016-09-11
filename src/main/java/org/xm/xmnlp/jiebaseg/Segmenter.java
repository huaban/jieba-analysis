package org.xm.xmnlp.jiebaseg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mingzai on 2016/9/10.
 */
public class Segmenter {
    private static Dict wordDict = Dict.getInstance();
    private FinalSegmenter  finalSegmenter = FinalSegmenter.getInstance();

    private Map<Integer, List<Integer>> createDAG(String sentence) {
        Map<Integer, List<Integer>> dag = new HashMap<>();
        Branch branch = wordDict.getBranch();
        char[] chars = sentence.toCharArray();
        int N = chars.length;
        int i = 0;
        int j = 0;
        while (i < N) {
            Match match = branch.match(chars, i, j - i + 1);
            if (match.isPrefix() || match.isMatch()) {
                if (match.isMatch()) {
                    if (!dag.containsKey(i)) {
                        List<Integer> value = new ArrayList<>();
                        dag.put(i, value);
                        value.add(j);
                    } else {
                        dag.get(i).add(j);
                    }
                }
                j += 1;
                if (j >= N) {
                    i += 1;
                    j = 1;
                }
            } else {
                i += 1;
                j = 1;
            }
        }
        for (i = 0; i < N; ++i) {
            if (!dag.containsKey(i)) {
                List<Integer> value = new ArrayList<>();
                value.add(i);
                dag.put(i, value);
            }
        }
        return dag;
    }


    private Map<Integer, Item<Integer>> calcRoute(String sentence, Map<Integer, List<Integer>> dag) {
        HashMap<Integer, Item<Integer>> route = new HashMap<>();
        int N = sentence.length();
        route.put(N, new Item(0, 0.0));
        for (int i = N - 1; i > -1; i--) {
            Item<Integer> item = null;
            for (Integer x : dag.get(i)) {
                String key = sentence.substring(i, x + 1);
                double freq = wordDict.getFreq(key) + route.get(x + 1).freq;
                String nature = wordDict.getNature(key);
                if (null == item) {
                    item = new Item(x, freq);
                } else if (item.freq < freq) {
                    item.freq = freq;
                    item.key = x;
                }
            }
            route.put(i, item);
        }
        return route;
    }

    public List<Token> process(String paragraph) {
        List<Token> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        int offset = 0;
        for (int i = 0; i < paragraph.length(); ++i) {
            char ch = CharUtil.regularize(paragraph.charAt(i));
            if (CharUtil.isCharFind(ch)) {
                sb.append(ch);
            } else {
                if (sb.length() > 0) {
                    for (String word : sentenceProcess(sb.toString())) {
                        String nature = wordDict.getNature(word);
                        tokens.add(new Token(word, offset, offset += word.length(), nature));
                    }

                    sb = new StringBuilder();
                    offset = i;
                }
                String key = paragraph.substring(i, i + 1);
                if (wordDict.containsWord(key)) {
                    tokens.add(new Token(key, offset, ++offset, wordDict.getNature(key)));
                } else {
                    // not in dict , most is punctuation
                    tokens.add(new Token(key, offset, ++offset, wordDict.getNature(key)));
                }
            }
        }

        if (sb.length() > 0) {
            for (String word : sentenceProcess(sb.toString())) {
                tokens.add(new Token(word, offset, offset += word.length(), wordDict.getNature(word)));
            }
        }

        return tokens;
    }

    private List<String> sentenceProcess(String sentence) {
        List<String> tokens = new ArrayList<>();
        int N = sentence.length();
        Map<Integer, List<Integer>> dag = createDAG(sentence);
        Map<Integer, Item<Integer>> route = calcRoute(sentence, dag);

        int x = 0;
        String buf;
        StringBuilder sb = new StringBuilder();
        while (x < N) {
            int y = route.get(x).key + 1;
            String lWord = sentence.substring(x, y);
            if (y - x == 1) {
                sb.append(lWord);
            } else {
                if (sb.length() > 0) {
                    buf = sb.toString();
                    add2Result(tokens, buf);
                    sb = new StringBuilder();
                }
                tokens.add(lWord);
            }
            x = y;
        }
        buf = sb.toString();
        if (buf.length() > 0) {
            add2Result(tokens, buf);
        }
        return tokens;
    }

    private void add2Result(List<String> tokens, String buf) {
        if (buf.length() == 1) {
            tokens.add(buf);
        } else {
            if (wordDict.containsWord(buf)) {
                tokens.add(buf);
            } else {
                // not in dict
                finalSegmenter.cut(buf, tokens);
            }

        }
    }


}
