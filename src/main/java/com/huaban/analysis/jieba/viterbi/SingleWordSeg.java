package com.huaban.analysis.jieba.viterbi;

import com.huaban.analysis.jieba.CharacterUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * <pre>
 *
 * Created by zhenqin.
 * User: zhenqin
 * Date: 17/7/13
 * Time: 11:45
 * Vendor: NowledgeData
 * To change this template use File | Settings | File Templates.
 *
 * </pre>
 *
 * @author zhenqin
 */
public class SingleWordSeg {

    private static SingleWordSeg singleInstance;

    private static Logger LOG = LoggerFactory.getLogger(FinalSeg.class);


    private SingleWordSeg() {
    }


    public synchronized static SingleWordSeg getInstance() {
        if (null == singleInstance) {
            singleInstance = new SingleWordSeg();
        }
        return singleInstance;
    }


    /**
     * 单字都是不要的。
     * @param sentence 句子
     * @param tokens 分词
     */
    public void cut(String sentence, List<String> tokens) {
        sentence = StringUtils.trimToNull(sentence);
        if(sentence == null) {
            return;
        }

        for (int i = 0; i < sentence.length(); i++) {
            char c = sentence.charAt(i);
            if(CharacterUtil.isChineseLetter(c)) {
                tokens.add(String.valueOf(c));
            }
        }
    }
}
