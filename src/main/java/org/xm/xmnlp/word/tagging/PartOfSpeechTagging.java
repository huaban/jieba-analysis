package org.xm.xmnlp.word.tagging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xm.xmnlp.word.WordSegmenter;
import org.xm.xmnlp.word.recognition.RecognitionTool;
import org.xm.xmnlp.word.segmentation.PartOfSpeech;
import org.xm.xmnlp.word.segmentation.Word;
import org.xm.xmnlp.word.util.DictionaryUtil;
import org.xm.xmnlp.word.util.GenericTrie;

import java.util.List;

/**
 * Created by xuming
 */
public class PartOfSpeechTagging {
    private static final Logger LOGGER = LoggerFactory.getLogger(PartOfSpeechTagging.class);
    private static final GenericTrie<String> GENERIC_TRIE = new GenericTrie<>();
    static {
        reload();
    }
    private static final String PATH = "/part_of_speech_dic.txt";
    public static void reload() {
        if (GENERIC_TRIE==null ) {
            load(DictionaryUtil.loadDictionaryFile(PATH));
        }
    }

    public static void clear(){
        GENERIC_TRIE.clear();
    }
    public static void add(String line){
        try{
            String[] attr = line.split(":");
            GENERIC_TRIE.put(attr[0],attr[1]);
        }catch (Exception e){
            LOGGER.error("error pos tag data:"+line);
        }
    }
    public static void load(List<String> lines){
        LOGGER.info("init pos tag");
        int count = 0;
        for(String line : lines){
            add(line);
        }
        LOGGER.info("init pos tag finished. count num:"+count);
    }

    public static void remove(String line){
        try {
            String[] attr = line.split(":");
            GENERIC_TRIE.remove(attr[0]);
        }catch (Exception e){
            LOGGER.error("error pos tag data:"+line);
        }
    }


    private PartOfSpeechTagging(){
    }

    public static void process(List<Word> words) {
        words.parallelStream()
                .forEach(word->{
                    if(word.getPartOfSpeech()!=null){
                        return;
                    }
                    String wordText = word.getText();
                    String pos = GENERIC_TRIE.get(wordText);
                    if(pos == null){
                        if(RecognitionTool.isEnglish(wordText)){
                            pos = "w";
                        }
                        if(RecognitionTool.isNumber(wordText)){
                            pos = "m";
                        }
                        if(RecognitionTool.isChineseNumber(wordText)){
                            pos ="mh";
                        }
                        if(RecognitionTool.isFraction(wordText)){
                            if(wordText.contains(".")||wordText.contains("．")||wordText.contains("·")){
                                pos = "mx";
                            }
                            if(wordText.contains("/")||wordText.contains("／")){
                                pos = "mf";
                            }
                        }
                        if(RecognitionTool.isQuantifier(wordText)) {
                            if (wordText.contains("‰") || wordText.contains("%") || wordText.contains("％")) {
                                pos = "mf";
                            }
                            //时间量词
                            else if (wordText.contains("时") || wordText.contains("分") || wordText.contains("秒")) {
                                pos = "tq";
                            }
                            //日期量词
                            else if (wordText.contains("年") || wordText.contains("月") || wordText.contains("日")
                                    || wordText.contains("天") || wordText.contains("号")) {
                                pos = "tdq";
                            }
                            //数量词
                            else {
                                pos = "mq";
                            }
                        }
                    }
                    word.setPartOfSpeech(PartOfSpeech.valueOf(pos));
                });
    }


    public static void main(String[] args){
        List<Word> words = WordSegmenter.seg("我爱你江山，我更爱美人。");
        System.out.println("未标注词性："+words);
        //词性标注
        PartOfSpeechTagging.process(words);
        System.out.println("标注词性："+words);
    }
}
