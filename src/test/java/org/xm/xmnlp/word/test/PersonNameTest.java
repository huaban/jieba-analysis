package org.xm.xmnlp.word.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xm.xmnlp.word.recognition.PersonName;
import org.xm.xmnlp.word.segmentation.Word;

import java.util.ArrayList;
import java.util.List;

import static org.xm.xmnlp.word.recognition.PersonName.*;

/**
 * Created by mingzai on 2016/9/13.
 */
public class PersonNameTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonName.class);

    public static void main(String[] args) {
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
