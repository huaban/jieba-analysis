package org.xm.xmnlp.word.dictionary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xm.xmnlp.word.dictionary.impl.DictionaryTrie;
import org.xm.xmnlp.word.recognition.PersonName;
import org.xm.xmnlp.word.util.WordConfTools;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Created by xuming
 */
public class DictionaryFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(DictionaryFactory.class);
    private static final int INTERCEPT_LENGTH = WordConfTools.getInt("intercept.length", 16);

    private DictionaryFactory() {
    }

    public static final Dictionary getDictionary() {
        return DictionaryHolder.DIC;
    }

    public static void reload() {
        DictionaryHolder.reload();
    }

    private static final class DictionaryHolder {
        private static final Dictionary DIC = constructDictionary();

        private static Dictionary constructDictionary() {
            try {
                String dicClass = WordConfTools.get("dic.class", "org.xm.xmnlp.word.dictionary.impl.DoubleArrayDictionaryTrie");
                LOGGER.info("dic.class = " + dicClass);
                return (Dictionary) Class.forName(dicClass.trim()).newInstance();
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                System.err.print("constuct dictionary failure:" + e.getMessage());
                throw new RuntimeException(e);
            }
        }

        static {
            reload();
        }

        public static void reload() {


        }

        public void clear() {
            DIC.clear();
        }

        public void load(List<String> lines) {
            LOGGER.info("init dictionary...");
            long start = System.currentTimeMillis();
            int count = 0;
            for(String surname: PersonName.getSurnames()){
                if(surname.length() ==2){
                    count ++;
                    lines.add(surname);
                }
            }
            LOGGER.info("add "+count + "surname to dictionary");
            List<String> words = getAllWords(lines);
            lines.clear();
            String dicDumpPath = WordConfTools.get("dic.dump.path");
            if(dicDumpPath!=null && dicDumpPath.length() >0){
                try{
                    Files.write(Paths.get(dicDumpPath),words);
                }catch (Exception e ){
                    LOGGER.error("dic dump error.",e);
                }
            }
            DIC.addAll(words);
            ShowStatistics(words);
            if(DIC instanceof DictionaryTrie){
                DictionaryTrie dictionaryTrie= (DictionaryTrie) DIC;
                dictionaryTrie.showConflict();
            }
            System.gc();
            LOGGER.info("init dictionary finished, spend time:"+(System.currentTimeMillis() -start)+" ms");
        }
        private void ShowStatistics(List<String> words){
            Map<Integer,AtomicInteger> map = new HashMap<>();
            words.forEach(i ->{
                map.putIfAbsent(i.length(),new AtomicInteger());
                map.get(i.length()).incrementAndGet();
            });
            int wordCount = 0;
            int totalLength = 0;
            for(int len :map.keySet()){
                totalLength +=len*map.get(len).get();
                wordCount +=map.get(len).get();
            }
            LOGGER.info("word count:"+wordCount+", maxlength of dictionary:"+DIC.getMaxLength());
            for(int len: map.keySet()){
                if(len<10){
                    LOGGER.info("word length :" + len +" the num of it:"+map.get(len));
                }else{
                    LOGGER.info("word length :" + len +" the num of it:"+map.get(len));
                }
            }
            LOGGER.info("average length of word ：" + (float) totalLength / wordCount);

        }

        private List<String> getWords(String line){
            List<String> words = new ArrayList<>();
            for(String word: line.split("\\s+")){
                if(word.length() >2 && word.contains(":")){
                    String[] attr = word.split(":");
                    if(attr !=null && attr.length >1){
                        word = attr[0];
                    }else{
                        word = null;
                    }
                }
                if(word!=null){
                    words.add(word);
                }
            }
            return words;
        }

        private List<String> getAllWords(List<String> lines){
            return lines.stream().flatMap(line ->getWords(line).stream())
                    .filter(w->w.length() <= INTERCEPT_LENGTH)
                    .collect(Collectors.toSet())
                    .stream()
                    .sorted()
                    .collect(Collectors.toList());
        }

    }
}
