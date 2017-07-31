package com.huaban.analysis.jieba;

import com.huaban.analysis.jieba.viterbi.FinalSeg;
import com.huaban.analysis.jieba.viterbi.SingleWordSeg;

import java.util.*;


/**
 *
 * 结巴分词，根据自定义词典分词实现, 不是自定义词全部不要。
 *
 * @author zhenqin
 */
public class JiebaCusDictSegmenter {
    //protected static SingleWordSeg seg = SingleWordSeg.getInstance();
    private static FinalSeg seg = FinalSeg.getInstance();


    private Map<Integer, List<Integer>> createDAG(String sentence, WordDictionary wordDict) {
        Map<Integer, List<Integer>> dag = new HashMap<Integer, List<Integer>>();
        DictSegment trie = wordDict.getTrie();
        char[] chars = sentence.toCharArray();
        int N = chars.length;
        int i = 0, j = 0;
        while (i < N) {
            Hit hit = trie.match(chars, i, j - i + 1);
            if (hit.isPrefix() || hit.isMatch()) {
                if (hit.isMatch()) {
                    if (!dag.containsKey(i)) {
                        List<Integer> value = new ArrayList<Integer>();
                        dag.put(i, value);
                        value.add(j);
                    }
                    else
                        dag.get(i).add(j);
                }
                j += 1;
                if (j >= N) {
                    i += 1;
                    j = i;
                }
            }
            else {
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


    private Map<Integer, Pair<Integer>> calc(String sentence, Map<Integer, List<Integer>> dag, WordDictionary wordDict) {
        int N = sentence.length();
        HashMap<Integer, Pair<Integer>> route = new HashMap<Integer, Pair<Integer>>();
        route.put(N, new Pair<Integer>(0, 0.0));
        for (int i = N - 1; i > -1; i--) {
            Pair<Integer> candidate = null;
            for (Integer x : dag.get(i)) {
                double freq = wordDict.getFreq(sentence.substring(i, x + 1)) + route.get(x + 1).freq;
                if (null == candidate) {
                    candidate = new Pair<Integer>(x, freq);
                }
                else if (candidate.freq < freq) {
                    candidate.freq = freq;
                    candidate.key = x;
                }
            }
            route.put(i, candidate);
        }
        return route;
    }


    /**
     * 按照 dict 的词库分词
     *
     * @param sentence
     * @param dict
     * @return
     */
    public List<String> sentenceProcess(String sentence, String... dict) {
        return sentenceProcess(sentence, Arrays.asList(dict), true);
    }


    /**
     *
     * 按照 dict 的词库分词
     *
     * @param sentence 句子
     * @param dict 词典
     * @return 返回分词
     */
    public List<String> sentenceProcess(String sentence, List<String> dict, boolean containsSingle) {
        return sentenceProcess(sentence, WordDictionary.newInstance(dict), containsSingle);
    }

    /**
     *
     * 按照 dict 的词库分词
     *
     * @param sentence 句子
     * @param wordDict 词典
     * @return 返回分词
     */
    public List<String> sentenceProcess(String sentence, WordDictionary wordDict, boolean containsSingle) {
        List<String> tokens = new ArrayList<String>();
        int N = sentence.length();
        Map<Integer, List<Integer>> dag = createDAG(sentence, wordDict);
        Map<Integer, Pair<Integer>> route = calc(sentence, dag, wordDict);

        int x = 0;
        int y = 0;
        String buf;
        StringBuilder sb = new StringBuilder();
        while (x < N) {
            y = route.get(x).key + 1;
            String lWord = sentence.substring(x, y);
            if (y - x == 1)
                sb.append(lWord);
            else {
                if (sb.length() > 0) {
                    buf = sb.toString();
                    sb = new StringBuilder();
                    if (buf.length() == 1) {
                        tokens.add(buf);
                    }
                    else {
                        if (wordDict.containsWord(buf)) {
                            tokens.add(buf);
                        } else if(containsSingle){
                            seg.cut(buf, tokens);
                        }
                    }
                }
                tokens.add(lWord);
            }
            x = y;
        }
        buf = sb.toString();
        if (buf.length() > 0) {
            if (buf.length() == 1) {
                tokens.add(buf);
            }
            else {
                if (wordDict.containsWord(buf)) {
                    tokens.add(buf);
                } else if(containsSingle){
                    seg.cut(buf, tokens);
                }
            }

        }
        return tokens;
    }


    public static void main(String[] args) {
        JiebaCusDictSegmenter jiebaCusDictSegmenter = new JiebaCusDictSegmenter();
        List<String> dict = Arrays.asList("读取 10", "保 100", "手动 10");
        List<String> strings = jiebaCusDictSegmenter.sentenceProcess("这种保存方法对数据读取有要求，需要手动指定读出来的数据的的dtype，如果指定的格式与保存时的不一致，则读出来的就是错误的数据。",
                dict, false);
        System.out.println(strings);
    }
}
