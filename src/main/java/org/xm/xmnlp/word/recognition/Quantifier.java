package org.xm.xmnlp.word.recognition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xm.xmnlp.word.util.DictionaryUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by mingzai on 2016/9/13.
 */
public class Quantifier {
    private static final Logger LOGGER = LoggerFactory.getLogger(Quantifier.class);

    private static final Set<Character> quantifiers = new HashSet<>();

    public static final Set<Character> getQuantifier(){
        return quantifiers;
    }
    static {
        reload();
    }

    private static final String PATH = "/quantifier.txt";

    public static void reload() {
        if (quantifiers == null || quantifiers.isEmpty()) {
            load(DictionaryUtil.loadDictionaryFile(PATH));
        }
    }

    public static void clear() {
        quantifiers.clear();
    }

    public static void add(String line) {
        if (line.length() == 1) {
            char c = line.charAt(0);
            quantifiers.add(c);
        } else {
            LOGGER.info("ignore illegal quantifier word:" + line);
        }
    }

    public static void load(List<String> lines) {
        lines.forEach(Quantifier::add);
        LOGGER.info("数量词初始化完毕，数量词个数：" + quantifiers.size());
    }

    public void remove(String line){
        if(line.length()==1){
            char c = line.charAt(0);
            quantifiers.remove(c);
        }else {
            LOGGER.info("ignore illegal quantifier word:"+line);
        }
    }

    public static boolean isQuantifier(char c) {
        return quantifiers.contains(c);
    }

    public static void main(String[] args){
        int i =1;
        for(char c:quantifiers){
            LOGGER.info((i++)+" : "+c);
        }
    }
}
