package org.xm.xmnlp.jiebaseg.demo;

import org.xm.xmnlp.jiebaseg.Segmenter;

/**
 * Created by xuming on 2016/5/27.
 */
public class MyDemo {

    public static void main(String[] args){
        Segmenter segmenter = new Segmenter();
        String[] sentences =
                new String[] {"他在普林斯顿思工作，你不懂电钻锥干嘛？很吓人，在黎明起来了，这几块地面积还真不小，研究生命的起源,他从马上下来，" +
                        "结婚的和尚未结婚的，阿丁说你很好，黎明认识这个李明不輸入簡體字典," +
                        "矿泉水瓶盖子下面有东西，點下面繁體字按鈕進行在線轉換、、你到底是何居心？" +
                        "这是一个伸手不见五指的黑夜。我叫孙悟空，我爱北京，我爱Python和C++。",
                        "我不喜欢日本和服。", "雷猴回归人间。",
                        "一次性交多少钱","我是中华人民共和国公民;我爸爸是共和党党员; 地铁和平门站," +
                        "我在深圳车公庙站，福田阳光高尔夫大厦海尔平安金融有限公司，德芙平安金融股份公司",
                        "清河县奥尼特羊绒纺织有限公司\n社会栋梁站起来。" +
                        "東海縣迅捷貿易有限責任公司"};
        for (String sentence : sentences) {
            System.out.println(segmenter.process(sentence).toString());
        }


    }
}
