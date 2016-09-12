package org.xm.xmnlp.word.segmentation.impl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xm.xmnlp.word.segmentation.Segmentation;
import org.xm.xmnlp.word.segmentation.SegmentationAlgorithm;
import org.xm.xmnlp.word.segmentation.Word;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


/**
 * Created by xuming
 */
public class PureEnglish implements Segmentation {
    private static final Logger LOGGER = LoggerFactory.getLogger(PureEnglish.class);
    private static final Pattern NUMBER = Pattern.compile("\\d+");
    private static final Pattern UNICODE = Pattern.compile("[uU][0-9a-fA-F]{4}");

    @Override
    public List<Word> seg(String text) {
        List<Word> segResult = new ArrayList<>();
        String[] words = text.trim().split("[^a-zA-Z0-9]");
        for (String word : words) {
            if (word.isEmpty() || word.length() < 2) {
                continue;
            }
            List<String> list = new ArrayList<>();
            if (word.length() < 6
                    || (Character.isUpperCase(word.charAt(word.length() - 1))
                    && Character.isUpperCase(word.charAt(0)))
                    || NUMBER.matcher(word).find()
                    || StringUtils.isAllUpperCase(word)) {
                word = word.toLowerCase();
            }
            int last = 0;
            for (int i = 1; i < word.length(); i++) {
                if (Character.isUpperCase(word.charAt(i)) && Character.isLowerCase(word.charAt(i - 1))) {
                    list.add(word.substring(last, i));
                    last = i;
                }
            }
            if (last < word.length()) {
                list.add(word.substring(last, word.length()));
            }
            list.stream()
                    .map(i -> i.toLowerCase())
                    .forEach(i -> {
                        if (i.length() < 2) {
                            return;
                        }
                        i = irregularity(i);
                        if (i != null) {
                            segResult.add(new Word(i));
                        }
                    });

        }
        return segResult;
    }

    private String irregularity(String word) {
        if (Character.isDigit(word.charAt(0))) {
            LOGGER.debug("start with num,ignore:" + word);
            return null;
        }
        if (word.startsWith("0x") || word.startsWith("0X")) {
            LOGGER.debug("词为16进制，忽略："+word);
            return null;
        }
        if (word.endsWith("l") && StringUtils.isNumeric(word.substring(0, word.length() - 1))) {
            LOGGER.debug("词为long类型数字，忽略："+word);
            return null;
        }
        if (UNICODE.matcher(word).find()) {
            LOGGER.debug("词为UNICODE字符编码，忽略："+word);
            return null;
        }
        switch (word) {
            case "doesn":
                return "does";
            case "isn":
                return "is";
            case "br":
                return null;
        }
        return word;
    }

    @Override
    public SegmentationAlgorithm getSegmentationAlgorithm() {
        return SegmentationAlgorithm.PureEnglish;
    }

    public static void main(String[] args){
        Segmentation seg = new PureEnglish();
        System.out.print(seg.seg("Your fucntion is also added permanently to" +
                " a new one , i need add 1+2=3.0 to 4 answer is 7.0," +
                " however this requires a small modifiction to Hive a Java " +
                "fiction. Think you!<br> xx "));
    }

}
