package org.xm.xmnlp.word.recognition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xm.xmnlp.word.util.DictionaryUtil;

import java.util.*;

/**
 * Created by xuming
 */
public class Punctuation {
    private static final Logger LOGGER = LoggerFactory.getLogger(Punctuation.class);
    private static char[] chars = null;
    private static final String PATH = "/punctuation.txt";
    static {
        reload();
    }
    public static void reload(){
        if(chars ==null||chars.length==0){
            load(DictionaryUtil.loadDictionaryFile(PATH));
        }

    }
    public static void load(List<String> lines){
        LOGGER.info("init punctuation");
        Set<Character> set = new HashSet<>();
        for(String line:lines){
            if(line .length() == 1){
                set.add(line.charAt(0));
            }else{
                LOGGER.warn("length is diff:"+line);
            }
        }
        set.add(' ');
        set.add('　');
        set.add('\t');
        set.add('\n');
        set.add('\r');
        List<Character> list = new ArrayList<>();
        list.addAll(set);
        Collections.sort(list);
        int len = list.size();
        chars = new char[len];
        for(int i =0;i<len ;i++){
            chars[i] = list.get(i);
        }
        set.clear();
        list.clear();
        LOGGER.info("init punctuation finished,count num:"+chars.length);
    }
    public static void clear(){
        chars =null;
    }

    public static void add(String line){
        if(line .length() !=1){
            LOGGER.warn("length is diff:"+line);
            return;
        }
        List<String> lines = new ArrayList<>();
        lines.add(line);
        if(chars !=null){
            for(char c:chars){
                lines.add(Character.toString(c));
            }
        }
        clear();
        load(lines);
    }
    public void remove(String line){
        if(line.length() !=1){
            LOGGER.warn("length is diff:"+line);
            return;
        }
        if(chars == null || chars.length <1){
            return;
        }
        List<String> lines = new ArrayList<>();
        for(char c: chars){
            lines.add(Character.toString(c));
        }
        int len = lines.size();
        lines.remove(line);
        if(len == lines.size()){
            return;
        }
        clear();
        load(lines);
    }

    public static List<String> seg(String text, boolean keepWhitespace) {
        return null;
    }
}
