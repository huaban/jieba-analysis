package org.xm.xmnlp.hanlp.dictionary.common;

import org.xm.xmnlp.hanlp.collection.trie.DoubleArrayTrie;
import org.xm.xmnlp.hanlp.corpus.dependency.CoNll.PosTagCompiler;
import org.xm.xmnlp.hanlp.corpus.synonym.Synonym;
import org.xm.xmnlp.hanlp.corpus.synonym.SynonymHelper;
import org.xm.xmnlp.hanlp.dictionary.CoreBiGramTableDictionary;
import org.xm.xmnlp.hanlp.seg.common.Term;
import org.xm.xmnlp.hanlp.tokenizer.StandardTokenizer;
import org.xm.xmnlp.hanlp.utility.Predefine;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.TreeMap;

import static org.xm.xmnlp.hanlp.utility.Predefine.logger;

/**
 * @author xuming
 */
public class CommonSynonymDictionary {
    public static class SynonymItem {
        public Synonym entry;
        public List<Synonym> synonymList;
        public Synonym.Type type;

        public SynonymItem(Synonym entry, List<Synonym> synonymList, Synonym.Type type) {
            this.entry = entry;
            this.synonymList = synonymList;
            this.type = type;
        }

        public SynonymItem(Synonym entry, List<Synonym> synonymList, char type) {
            this.entry = entry;
            this.synonymList = synonymList;
            switch (type) {
                case '=':
                    this.type = Synonym.Type.EQUAL;
                    break;
                case '#':
                    this.type = Synonym.Type.LIKE;
                    break;
                default:
                    this.type = Synonym.Type.SINGLE;
                    break;
            }
        }

        public Synonym randomSynonym() {
            return randomSynonym(null, null);
        }

        public Synonym randomSynonym(Synonym.Type type, String preWord) {
            ArrayList<Synonym> synonymArrayList = new ArrayList<>(synonymList);
            ListIterator<Synonym> listIterator = synonymArrayList.listIterator();
            if (type != null) {
                while (listIterator.hasNext()) {
                    Synonym synonym = listIterator.next();
                    if (synonym.type != type || (preWord != null && CoreBiGramTableDictionary.getBiFrequency(preWord, synonym.realWord) == 0)) {
                        listIterator.remove();
                    }
                }
            }
            if (synonymArrayList.size() == 0) {
                return null;
            }
            return synonymArrayList.get((int) (System.currentTimeMillis() % (long) synonymArrayList.size()));
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append(entry);
            sb.append(' ');
            sb.append(type);
            sb.append(' ');
            sb.append(synonymList);
            return sb.toString();
        }

        public long distance(SynonymItem other) {
            return entry.distance(other.entry);
        }

        public static SynonymItem createUndefined(String word) {
            SynonymItem item = new SynonymItem(new Synonym(word, word.hashCode() * 100000 + Long.MAX_VALUE / 3, Synonym.Type.UNDEFINED), null, Synonym.Type.UNDEFINED);
            return item;
        }
    }

    DoubleArrayTrie<SynonymItem> trie;
    private long maxSynonymItemIdDistance;

    private CommonSynonymDictionary() {

    }

    public static CommonSynonymDictionary create(InputStream inputStream) {
        CommonSynonymDictionary dictionary = new CommonSynonymDictionary();
        if (dictionary.load(inputStream)) {
            return dictionary;
        }
        return null;
    }

    public boolean load(InputStream inputStream) {
        trie = new DoubleArrayTrie<>();
        TreeMap<String, SynonymItem> treeMap = new TreeMap<>();
        String line = null;
        try {
            BufferedReader bw = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            ArrayList<Synonym> synonymList = null;
            while ((line = bw.readLine()) != null) {
                String[] args = line.split(" ");
                synonymList = Synonym.create(args);
                char type = args[0].charAt(args[0].length() - 1);
                for (Synonym synonym : synonymList) {
                    treeMap.put(synonym.realWord, new SynonymItem(synonym, synonymList, type));
                }
            }
            bw.close();
            if (synonymList != null && synonymList.size() > 0) {
                maxSynonymItemIdDistance = synonymList.get(synonymList.size() - 1).id - SynonymHelper.convertString2IdWithIndex("Aa01A01", 0) + 1;
            }
            int resultCode = trie.build(treeMap);
            if (resultCode != 0) {
                logger.warning("build " + inputStream + "failure,error code:" + resultCode);
                return false;
            }
        } catch (Exception e) {
            logger.warning("read " + inputStream + " failure, error with " + line);
            return false;
        }
        return true;
    }

    public SynonymItem get(String key) {
        return trie.get(key);
    }

    public long getMaxSynonymItemIdDistance() {
        return maxSynonymItemIdDistance;
    }

    public long distance(String a, String b) {
        SynonymItem itemA = get(a);
        if (itemA == null) {
            return Long.MAX_VALUE / 3;
        }
        SynonymItem itemB = get(b);
        if (itemB == null) {
            return Long.MAX_VALUE / 3;
        }
        return itemA.distance(itemB);
    }

    public String rewriteQuickly(String text) {
        assert text != null;
        StringBuilder sbOut = new StringBuilder((int) (text.length() * 1.2));
        String preWord = Predefine.TAG_BIGIN;
        for (int i = 0; i < text.length(); ++i) {
            int state = 1;
            state = trie.transition(text.charAt(i), state);
            if (state > 0) {


                int start = i;
                int to = i + 1;
                int end = -1;
                SynonymItem value = null;
                for (; to < text.length(); ++to) {
                    state = trie.transition(text.charAt(to), state);
                    if (state < 0) break;
                    SynonymItem output = trie.output(state);
                    if (output != null) {
                        value = output;
                        end = to + 1;
                    }
                }
                if (value != null) {
                    Synonym synonym = value.randomSynonym(Synonym.Type.EQUAL, preWord);
                    if (synonym != null) {
                        sbOut.append(synonym.realWord);
                        preWord = synonym.realWord;
                    } else {
                        preWord = text.substring(start, end);
                        sbOut.append(preWord);
                    }
                    i = end - 1;
                } else {
                    preWord = String.valueOf(text.charAt(i));
                    sbOut.append(text.charAt(i));
                }
            } else {
                preWord = String.valueOf(text.charAt(i));
                sbOut.append(text.charAt(i));
            }
        }
        return sbOut.toString();
    }

    public String rewrite(String text) {
        List<Term> termList = StandardTokenizer.segment(text.toCharArray());
        StringBuilder sbOut = new StringBuilder((int) (text.length() * 1.2));
        String preWord = Predefine.TAG_BIGIN;
        for (Term term : termList) {
            SynonymItem synonymItem = get(term.word);
            Synonym synonym;
            if (synonymItem != null && (synonym = synonymItem.randomSynonym(Synonym.Type.EQUAL, preWord)) != null) {
                sbOut.append(synonym.realWord);
            } else sbOut.append(term.word);
            preWord = PosTagCompiler.compile(term.nature.toString(), term.word);
        }
        return sbOut.toString();
    }
}
