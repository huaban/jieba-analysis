package org.xm.xmnlp.jiebaseg.demo;

import org.xm.xmnlp.jiebaseg.Dict;
import org.xm.xmnlp.jiebaseg.Segmenter;

import java.nio.file.Paths;

/**
 * Created by mingzai on 2016/9/11.
 */
public class UserDemo {
    public static void main(String[] args){
        Dict.getInstance().init(Paths.get("conf"));//取最外层的conf文件夹（与src文件夹并列）的所有数据
        Segmenter segmenter = new Segmenter();
        String[] sentences =
                new String[] {"他从马上下来点赞，他在普顿思工作，你不懂电钻锥干嘛？很吓人，在黎明起来了，" +
                        "这几块地面积还真不小，研究生命的起源," +
                        "结婚的和尚未结婚的，阿丁说你很好，黎明认识这个李明不輸入簡體字典,"};
        for (String sentence : sentences) {
            System.out.println(segmenter.process(sentence).toString());
        }


    }


}
