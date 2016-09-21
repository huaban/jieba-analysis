/*
 * <summary></summary>
 * <author>He Han</author>
 * <email>hankcs.cn@gmail.com</email>
 * <create-date>2014/12/7 19:25</create-date>
 *
 * <copyright file="DemoChineseNameRecoginiton.java" company="上海林原信息科技有限公司">
 * Copyright (c) 2003-2014+ 上海林原信息科技有限公司. All Right Reserved+ http://www.linrunsoft.com/
 * This source is subject to the LinrunSpace License. Please contact 上海林原信息科技有限公司 to get more information.
 * </copyright>
 */
package org.xm.xmnlp.hanlp.demo;


import org.xm.xmnlp.hanlp.HanLP;
import org.xm.xmnlp.hanlp.seg.Segment;
import org.xm.xmnlp.hanlp.seg.common.Term;

import java.util.List;

/**
 * 地名识别
 * @author hankcs
 */
public class DemoPlaceRecognition
{
    public static void main(String[] args)
    {
        HanLP.Config.enableDebug();
        String[] testCase = new String[]{
                "蓝翔给宁夏固原市彭阳县红河镇黑牛沟村捐赠了挖掘机",
                "蓝翔给宁夏固原彭阳金华红河镇黑牛沟捐赠了挖掘机，湖北辉煌安陆县镇海镇服装有限公司",
        };
        Segment segment = HanLP.newSegment().enablePlaceRecognize(true);
        for (String sentence : testCase)
        {
            List<Term> termList = segment.seg(sentence);
            System.out.println(termList);
        }
    }
}
