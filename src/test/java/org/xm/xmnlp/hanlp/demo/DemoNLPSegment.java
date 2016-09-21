/*
 * <summary></summary>
 * <author>He Han</author>
 * <email>hankcs.cn@gmail.com</email>
 * <create-date>2014/12/7 19:13</create-date>
 *
 * <copyright file="DemoNLPSegment.java" company="上海林原信息科技有限公司">
 * Copyright (c) 2003-2014, 上海林原信息科技有限公司. All Right Reserved, http://www.linrunsoft.com/
 * This source is subject to the LinrunSpace License. Please contact 上海林原信息科技有限公司 to get more information.
 * </copyright>
 */
package org.xm.xmnlp.hanlp.demo;


import org.xm.xmnlp.hanlp.HanLP;
import org.xm.xmnlp.hanlp.seg.common.Term;
import org.xm.xmnlp.hanlp.tokenizer.NLPTokenizer;

import java.util.List;

/**
 * NLP分词
 *
 * @author hankcs
 */
public class DemoNLPSegment {
    public static void main(String[] args) {
        HanLP.Config.enableDebug();
        List<Term> termList = NLPTokenizer.segment("上外日本文化经济学院的陆晚霞教授正在教授泛读课程，总统对你有意见，总统有意来见你。韩信尚能饭否？lili给我的第九千九百九十九朵玫瑰");
        System.out.println(termList);
    }
}
