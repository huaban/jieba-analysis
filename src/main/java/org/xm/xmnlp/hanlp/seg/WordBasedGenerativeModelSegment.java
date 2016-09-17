package org.xm.xmnlp.hanlp.seg;

import org.xm.xmnlp.hanlp.algorithm.Viterbi;
import org.xm.xmnlp.hanlp.collection.trie.DoubleArrayTrie;
import org.xm.xmnlp.hanlp.corpus.tag.Nature;
import org.xm.xmnlp.hanlp.dictionary.CoreDictionary;
import org.xm.xmnlp.hanlp.dictionary.CoreDictionaryTransformMatrixDictionary;
import org.xm.xmnlp.hanlp.seg.common.Term;
import org.xm.xmnlp.hanlp.seg.common.Vertex;
import org.xm.xmnlp.hanlp.seg.common.WordNet;
import org.xm.xmnlp.hanlp.utility.TextUtility;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * @author xuming
 */
public abstract class WordBasedGenerativeModelSegment extends Segment {
    public WordBasedGenerativeModelSegment() {
        super();
    }

    protected static void GenerateWord(List<Vertex> linkedArray, WordNet wordNetOptimum) {
        fixResultByRule(linkedArray);
        wordNetOptimum.addAll(linkedArray);
    }

    protected static void fixResultByRule(List<Vertex> linkedArray) {
        //Merge all seperate continue num into one number
        mergeContinueNumIntoOne(linkedArray);
        //The delimiter "－－"
        ChangeDelimiterPOS(linkedArray);
        //如果前一个词是数字，当前词以“－”或“-”开始，并且不止这一个字符，
        //那么将此“－”符号从当前词中分离出来。
        //例如 “3 / -4 / 月”需要拆分成“3 / - / 4 / 月”
        SplitMiddelSlashFromDigitalWords(linkedArray);
        //1、如果当前词是数字，下一个词是“月、日、时、分、秒、月份”中的一个，则合并,且当前词词性是时间
        //2、如果当前词是可以作为年份的数字，下一个词是“年”，则合并，词性为时间，否则为数字。
        //3、如果最后一个汉字是"点" ，则认为当前数字是时间
        //4、如果当前串最后一个汉字不是"∶·．／"和半角的'.''/'，那么是数
        //5、当前串最后一个汉字是"∶·．／"和半角的'.''/'，且长度大于1，那么去掉最后一个字符。例如"1."
        CheckDateElements(linkedArray);
    }

    private static void mergeContinueNumIntoOne(List<Vertex> linkedArray) {
        if (linkedArray.size() < 2) return;
        ListIterator<Vertex> listIterator = linkedArray.listIterator();
        Vertex next = listIterator.next();
        Vertex current = next;
        while (listIterator.hasNext()) {
            next = listIterator.next();
            if ((TextUtility.isAllNum(current.realWord) || TextUtility.isAllChineseNum(current.realWord)) && (TextUtility.isAllNum(next.realWord) || TextUtility.isAllChineseNum(next.realWord))) {
                // 这部分从逻辑上等同于current.realWord = current.realWord + next.realWord;
                // 但是current指针被几个路径共享，需要备份，不然修改了一处就修改了全局
                current = Vertex.newNumberInstance(current.realWord + next.realWord);
                listIterator.previous();
                listIterator.previous();
                listIterator.set(current);
                listIterator.next();
                listIterator.next();
                listIterator.remove();
            } else {
                current = next;
            }
        }
    }

    private static void ChangeDelimiterPOS(List<Vertex> linkedArray) {
        for (Vertex vertex : linkedArray) {
            if (vertex.realWord.equals("－－") || vertex.realWord.equals("—") || vertex.realWord.equals("-")) {
                vertex.confirmNature(Nature.w);
            }
        }
    }

    private static void SplitMiddelSlashFromDigitalWords(List<Vertex> linkedArray) {
        if (linkedArray.size() < 2)
            return;

        ListIterator<Vertex> listIterator = linkedArray.listIterator();
        Vertex next = listIterator.next();
        Vertex current = next;
        while (listIterator.hasNext()) {
            next = listIterator.next();
//            System.out.println("current:" + current + " next:" + next);
            Nature currentNature = current.getNature();
            if (currentNature == Nature.nx && (next.hasNature(Nature.q) || next.hasNature(Nature.n))) {
                String[] param = current.realWord.split("-", 1);
                if (param.length == 2) {
                    if (TextUtility.isAllNum(param[0]) && TextUtility.isAllNum(param[1])) {
                        current = current.copy();
                        current.realWord = param[0];
                        current.confirmNature(Nature.m);
                        listIterator.previous();
                        listIterator.previous();
                        listIterator.set(current);
                        listIterator.next();
                        listIterator.add(Vertex.newPunctuationInstance("-"));
                        listIterator.add(Vertex.newNumberInstance(param[1]));
                    }
                }
            }
            current = next;
        }
    }

    private static void CheckDateElements(List<Vertex> linkedArray) {
        if (linkedArray.size() < 2)
            return;
        ListIterator<Vertex> listIterator = linkedArray.listIterator();
        Vertex next = listIterator.next();
        Vertex current = next;
        while (listIterator.hasNext()) {
            next = listIterator.next();
            if (TextUtility.isAllNum(current.realWord) || TextUtility.isAllChineseNum(current.realWord)) {
                //===== 1、如果当前词是数字，下一个词是“月、日、时、分、秒、月份”中的一个，则合并且当前词词性是时间
                String nextWord = next.realWord;
                if ((nextWord.length() == 1 && "月日时分秒".contains(nextWord)) || (nextWord.length() == 2 && nextWord.equals("月份"))) {
                    current = Vertex.newTimeInstance(current.realWord + next.realWord);
                    listIterator.previous();
                    listIterator.previous();
                    listIterator.set(current);
                    listIterator.next();
                    listIterator.next();
                    listIterator.remove();
                }
                //===== 2、如果当前词是可以作为年份的数字，下一个词是“年”，则合并，词性为时间，否则为数字。
                else if (nextWord.equals("年")) {
                    if (TextUtility.isYearTime(current.realWord)) {
                        current = Vertex.newTimeInstance(current.realWord + next.realWord);
                        listIterator.previous();
                        listIterator.previous();
                        listIterator.set(current);
                        listIterator.next();
                        listIterator.next();
                        listIterator.remove();
                    }
                    //===== 否则当前词就是数字了 =====
                    else {
                        current.confirmNature(Nature.m);
                    }
                } else {
                    //===== 3、如果最后一个汉字是"点" ，则认为当前数字是时间
                    if (current.realWord.endsWith("点")) {
                        current.confirmNature(Nature.t, true);
                    } else {
                        char[] tmpCharArray = current.realWord.toCharArray();
                        String lastChar = String.valueOf(tmpCharArray[tmpCharArray.length - 1]);
                        //===== 4、如果当前串最后一个汉字不是"∶·．／"和半角的'.''/'，那么是数
                        if (!"∶·．／./".contains(lastChar)) {
                            current.confirmNature(Nature.m, true);
                        }
                        //===== 5、当前串最后一个汉字是"∶·．／"和半角的'.''/'，且长度大于1，那么去掉最后一个字符。例如"1."
                        else if (current.realWord.length() > 1) {
                            char last = current.realWord.charAt(current.realWord.length() - 1);
                            current = Vertex.newNumberInstance(current.realWord.substring(0, current.realWord.length() - 1));
                            listIterator.previous();
                            listIterator.previous();
                            listIterator.set(current);
                            listIterator.next();
                            listIterator.add(Vertex.newPunctuationInstance(String.valueOf(last)));
                        }
                    }
                }
            }
            current = next;
        }
    }

    protected void GenerateWordNet(final WordNet wordNetStorage) {
        final char[] charArray = wordNetStorage.charArray;
        DoubleArrayTrie<CoreDictionary.Attribute>.Searcher searcher = CoreDictionary.trie.getSearcher(charArray, 0);
        while (searcher.next()) {
            wordNetStorage.add(searcher.begin + 1, new Vertex(new String(charArray, searcher.begin, searcher.length), searcher.value, searcher.index));
        }
        LinkedList<Vertex>[] vertexes = wordNetStorage.getVertexes();
        for (int i = 1; i < vertexes.length; ) {
            if (vertexes[i].isEmpty()) {
                int j = i + 1;
                for (; j < vertexes.length - 1; ++j) {
                    if (!vertexes[j].isEmpty()) break;
                }
                wordNetStorage.add(i, quickAtomSegment(charArray, i - 1, j - 1));
                i = j;
            } else {
                i += vertexes[i].getLast().realWord.length();
            }
        }
    }

    private static Term convert(Vertex vertex) {
        return new Term(vertex.realWord, vertex.guessNature());
    }

    protected static void speechTagging(List<Vertex> vertexList) {
        Viterbi.compute(vertexList, CoreDictionaryTransformMatrixDictionary.transformMatrixDictionary);
    }

    protected static List<Term> getIndexResult(List<Vertex> vertexList, WordNet wordNetAll) {
        List<Term> termList = new LinkedList<>();
        int line = 1;
        ListIterator<Vertex> listIterator = vertexList.listIterator();
        listIterator.next();
        int length = vertexList.size() - 2;
        for (int i = 0; i < length; ++i) {
            Vertex vertex = listIterator.next();
            Term termMain = convert(vertex);
            termList.add(termMain);
            termMain.offset = line - 1;
            if (vertex.realWord.length() > 2) {
                int currentLine = line;
                while (currentLine < line + vertex.realWord.length()) {
                    List<Vertex> vertexListCurrentLine = wordNetAll.get(currentLine);    // 这一行的词
                    for (Vertex smallVertex : vertexListCurrentLine) // 这一行的短词
                    {
                        if (((termMain.nature == Nature.mq
                                && smallVertex.hasNature(Nature.q))
                                || smallVertex.realWord.length() > 1)
                                && smallVertex != vertex) {
                            listIterator.add(smallVertex);
                            Term termSub = convert(smallVertex);
                            termSub.offset = currentLine - 1;
                            termList.add(termSub);
                        }
                    }
                    ++currentLine;
                }
            }
            line += vertex.realWord.length();
        }

        return termList;
    }
}
