package org.xm.xmnlp.word.recognition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xm.xmnlp.word.segmentation.PartOfSpeech;
import org.xm.xmnlp.word.segmentation.Word;
import org.xm.xmnlp.word.tagging.PartOfSpeechTagging;
import org.xm.xmnlp.word.util.DictionaryUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Created by xuming
 */
public class PersonName {
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonName.class);
    private static final Set<String> SURNAME_1 = new HashSet<>();
    private static final Set<String> SURNAME_2 = new HashSet<>();
    private static final Map<String, Integer> POS_SEQ = new HashMap<>();

    static {
        reload();
    }

    private static final String PATH = "/surname.txt";

    public static void reload() {
        if (SURNAME_1 == null || SURNAME_1.isEmpty()) {
            load(DictionaryUtil.loadDictionaryFile(PATH));
        }
    }

    public static void clear() {
        SURNAME_1.clear();
        SURNAME_2.clear();
        POS_SEQ.clear();
    }

    public static void load(List<String> lines) {
        LOGGER.info("init person name");
        for (String line : lines) {
            add(line);
        }
        LOGGER.info("init person name finished, single surname num:" + SURNAME_1.size() + ", double surname num:" + SURNAME_2.size());
    }

    public static void add(String line) {
        if (line.length() == 1) {
            SURNAME_1.add(line);
        } else if (line.length() == 2) {
            SURNAME_2.add(line);
        } else if (line.startsWith("pos_seq=")) {
            String[] attr = line.split("=");
            POS_SEQ.put(attr[1].trim().replaceAll("\\s", " "), Integer.parseInt(attr[2]));
        } else {
            LOGGER.error("person name error:" + line);
        }
    }

    public static void remove(String line) {
        if (line.length() == 1) {
            SURNAME_1.remove(line);
        } else if (line.length() == 2) {
            SURNAME_2.remove(line);
        } else if (line.startsWith("pos_seq=")) {
            String[] attr = line.split("=");
            POS_SEQ.remove(attr[1].trim().replaceAll("\\s", " "));
        } else {
            LOGGER.error("person name error:" + line);
        }
    }

    public static List<String> getSurnames() {
        List<String> result = new ArrayList<>();
        result.addAll(SURNAME_1);
        result.addAll(SURNAME_2);
        Collections.sort(result);
        return result;
    }

    public static String getSurname(String name) {
        if (isPersonName(name)) {
            if (isSurname(name.substring(0, 2))) {
                return name.substring(0, 2);
            }
            if (isSurname(name.substring(0, 1))) {
                return name.substring(0, 1);
            }
        }
        return "";
    }

    public static boolean isSurname(String text) {
        return SURNAME_1.contains(text) || SURNAME_2.contains(text);
    }

    public static boolean isPersonName(String text) {
        int len = text.length();
        if (len < 2) {
            return false;
        }
        if (len == 2) {
            return SURNAME_1.contains(text.substring(0, 1));
        }
        if (len == 3) {
            return SURNAME_1.contains(text.substring(0, 1)) || SURNAME_2.contains(text.substring(0, 2));
        }
        if (len == 4) {
            return SURNAME_2.contains(text.substring(0, 2));
        }
        return false;
    }

    public static List<Word> recognize(List<Word> words) {
        int len = words.size();
        if (len < 2) {
            return words;
        }
        LOGGER.debug("name recognize:" + words);
        List<List<Word>> select = new ArrayList<>();
        List<Word> result = new ArrayList<>();
        for (int i = 0; i < len - 1; i++) {
            String word = words.get(i).getText();
            if (isSurname(word)) {
                result.addAll(recognizePersonName(words.subList(i, words.size())));
                select.add(result);
                result = new ArrayList<>(words.subList(0, i + 1));
            } else {
                result.add(new Word(word));
            }
        }
        if (select.isEmpty()) {
            return words;
        }
        if (select.size() == 1) {
            return select.get(0);
        }
        return selectBest(select);
    }

    private static List<Word> selectBest(List<List<Word>> words) {
        LOGGER.debug("slect the best result from:" + words);
        Map<List<Word>, Integer> map = new ConcurrentHashMap<>();
        AtomicInteger i = new AtomicInteger();
        words.stream()
                .forEach(word -> {
                    LOGGER.info(i.incrementAndGet() + ",start deal with:" + word);
                    PartOfSpeechTagging.process(word);
                    StringBuilder seq = new StringBuilder();
                    word.forEach(w -> seq.append(w.getPartOfSpeech().getPos().charAt(0)).append(" "));
                    String seqStr = seq.toString();
                    AtomicInteger score = new AtomicInteger();
                    LOGGER.info("词序列：{} 的词序列：{}", word, seqStr);
                    POS_SEQ.keySet().parallelStream().forEach(pos_seq -> {
                        if (seqStr.contains(pos_seq)) {
                            int sc = POS_SEQ.get(pos_seq);
                            LOGGER.info(pos_seq + "词序增加分值：" + sc);
                            score.addAndGet(sc);
                        }
                    });
                    score.addAndGet(-word.size());
                    LOGGER.debug("长度的负值也作为分值：" + (-word.size()));
                    LOGGER.debug("评分结果：" + score.get());
                    map.put(word, score.get());
                });
        List<Word> result = map.entrySet()
                .parallelStream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .map(e -> e.getKey())
                .collect(Collectors.toList())
                .get(0);
        LOGGER.debug("result is:" + result);
        return result;
    }

    private static List<Word> recognizePersonName(List<Word> words) {
        int len = words.size();
        if (len < 2) {
            return words;
        }
        List<Word> result = new ArrayList<>();
        for (int i = 0; i < len - 1; i++) {
            String second = words.get(i + 1).getText();
            if (second.length() > 1) {
                result.add(new Word(words.get(i).getText()));
                result.add(new Word(words.get(i + 1).getText()));
                i++;
                if (i == len - 2) {
                    result.add(new Word(words.get(i + 1).getText()));
                }
                continue;
            }
            String first = words.get(i).getText();
            if (isSurname(first)) {
                String third = "";
                if (i + 2 < len && words.get(i + 2).getText().length() == 1) {
                    third = words.get(i + 2).getText();
                }
                String text = first + second + third;
                if (isPersonName(text)) {
                    LOGGER.debug("recognize person name:" + text);
                    Word word = new Word(text);
                    word.setPartOfSpeech(PartOfSpeech.valueOf("nr"));
                    result.add(word);
                    i++;
                    if (!"".equals(third)) {
                        i++;
                    }
                } else {
                    result.add(new Word(first));
                }
            } else {
                result.add(new Word(first));
            }
            if (i == len - 2) {
                result.add(new Word(words.get(i + 1).getText()));
            }
        }
        return result;
    }

    public static void main(String[] args) {
        int i = 1;
        for (String str : SURNAME_1) {
            LOGGER.info((i++) + " : " + str);
        }
        for (String str : SURNAME_2) {
            LOGGER.info((i++) + " : " + str);
        }
        LOGGER.info("杨尚川：" + isPersonName("杨尚川"));
        LOGGER.info("欧阳飞燕：" + isPersonName("欧阳飞燕"));
        LOGGER.info("令狐冲：" + isPersonName("令狐冲"));
        List<Word> test = new ArrayList<>();
        test.add(new Word("快"));
        test.add(new Word("来"));
        test.add(new Word("看"));
        test.add(new Word("杨"));
        test.add(new Word("尚"));
        test.add(new Word("川"));
        test.add(new Word("表演"));
        test.add(new Word("魔术"));
        test.add(new Word("了"));
        LOGGER.info(recognize(test).toString());

        test = new ArrayList<>();
        test.add(new Word("李"));
        test.add(new Word("世"));
        test.add(new Word("明"));
        test.add(new Word("的"));
        test.add(new Word("昭仪"));
        test.add(new Word("欧阳"));
        test.add(new Word("飞"));
        test.add(new Word("燕"));
        test.add(new Word("其实"));
        test.add(new Word("很"));
        test.add(new Word("厉害"));
        test.add(new Word("呀"));
        test.add(new Word("！"));
        test.add(new Word("比"));
        test.add(new Word("公孙"));
        test.add(new Word("黄"));
        test.add(new Word("后"));
        test.add(new Word("牛"));
        LOGGER.info(recognize(test).toString());

        test = new ArrayList<>();
        test.add(new Word("发展"));
        test.add(new Word("中国"));
        test.add(new Word("家兔"));
        test.add(new Word("的"));
        test.add(new Word("计划"));
        LOGGER.info(recognize(test).toString());

        test = new ArrayList<>();
        test.add(new Word("杨尚川"));
        test.add(new Word("好"));
        LOGGER.info(recognize(test).toString());

        LOGGER.info(getSurname("欧阳锋"));
        LOGGER.info(getSurname("李阳锋"));
    }

}
