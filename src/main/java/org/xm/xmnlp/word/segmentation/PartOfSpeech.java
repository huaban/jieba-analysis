package org.xm.xmnlp.word.segmentation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xm.xmnlp.word.util.DictionaryUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by mingzai on 2016/9/11.
 */
public class PartOfSpeech {
    private static final Logger LOGGER = LoggerFactory.getLogger(PartOfSpeech.class);
    private String pos;
    private String des;

    public PartOfSpeech(String pos, String des) {
        this.pos = pos;
        this.des = des;
    }

    private static class PartOfSpeechMap {
        private static final Map<String, PartOfSpeech> POS = new HashMap<>();
        private static final String PATH = "/part_of_speech_des.txt";
        static {
            reload();
        }

        public static void reload() {
            if (POS == null || POS.isEmpty()) {
                load(DictionaryUtil.loadDictionaryFile(PATH));
            }
        }

        public static void clear() {
            POS.clear();
        }

        public static void load(List<String> lines) {
            LOGGER.info("init POS...");
            int count = 0;
            for (String line : lines) {
                try {
                    String[] attr = line.split("=");
                    POS.put(attr[0], new PartOfSpeech(attr[0], attr[1]));
                    count++;
                } catch (Exception e) {
                    LOGGER.error("POS data error:" + line);
                }
            }
            LOGGER.info("load POS finished, count num:" + count);
        }

        public static void add(String line) {
            try {
                String[] attr = line.split("=");
                POS.put(attr[0], new PartOfSpeech(attr[0], attr[1]));
            } catch (Exception e) {
                LOGGER.error("POS data error:" + line);
            }
        }

        public static void remove(String line) {
            try {
                String[] attr = line.split("=");
                POS.remove(attr[0]);
            } catch (Exception e) {
                LOGGER.error("POS data error:" + line);
            }
        }

        private static Map<String, PartOfSpeech> getPos() {
            return POS;
        }
    }

    public static PartOfSpeech valueOf(String pos) {
        if (Objects.isNull(pos) || "".equals(pos.trim())) {
            return I;
        }
        PartOfSpeech partOfSpeech = PartOfSpeechMap.getPos().get(pos.toLowerCase());
        if (partOfSpeech == null) {
            return new PartOfSpeech(pos, "");
        }
        return partOfSpeech;
    }

    public static boolean isPos(String pos) {
        return PartOfSpeechMap.getPos().get(pos.toLowerCase()) != null;
    }

    //未知词性
    public static final PartOfSpeech I = new PartOfSpeech("i", "未知");

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public static void main(String[] args) {
        System.out.println(PartOfSpeech.isPos("n"));
        System.out.println(PartOfSpeech.isPos("ns"));
        System.out.println(PartOfSpeech.isPos("nn"));
        System.out.println(PartOfSpeech.I.getPos() + " " + PartOfSpeech.I.getDes());
        PartOfSpeech N_ANIMAL = new PartOfSpeech("n_animal", "动物");
        System.out.println(N_ANIMAL.getPos() + " " + N_ANIMAL.getDes());
    }

}
