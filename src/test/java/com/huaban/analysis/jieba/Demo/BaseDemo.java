package com.huaban.analysis.jieba.Demo;

import com.huaban.analysis.jieba.CharacterUtil;
import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.WordDictionary;

import java.nio.file.Paths;

/**
 * Created by xuming on 2016/5/27.
 */
public class BaseDemo {

    public static void main(String[] args){

        WordDictionary.getInstance().init(Paths.get("conf"));//取最外层的conf文件夹（与src文件夹并列）的所有数据

        System.out.println("out A is :" + CharacterUtil.regularize('ｆ'));

        JiebaSegmenter segmenter = new JiebaSegmenter();

//        TxtUtil txtUtil = new TxtUtil();
//        String inputFile = "D:\\PyCredit\\NLP\\company name 2000 row.txt";
//        String outputFile = "D:\\PyCredit\\NLP\\out.txt";
//        List<String> readResult = new ArrayList<>();
//        try {
//            readResult = txtUtil.readTxt(inputFile);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        List<String> outStringList = new ArrayList<>();
//
//        for (String i : readResult) {
//            System.out.println(segmenter.process(i, JiebaSegmenter.SegMode.SEARCH).toString());
//            outStringList.add(segmenter.process(i, JiebaSegmenter.SegMode.SEARCH).toString());
//        }
//        try {
//            txtUtil.writeTxt(outStringList,outputFile);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        String[] sentences =
                new String[] {"他在黎明起来了，阿丁说你很好，黎明认识这个李明不輸入簡體字典,矿泉水瓶盖子下面有东西，點下面繁體字按鈕進行在線轉換、、你到底是何居心？这是一个伸手不见五指的黑夜。我叫孙悟空，我爱北京，我爱Python和C++。", "我不喜欢日本和服。", "雷猴回归人间。",
                        "工信处女干事每月经过下属科室都要亲口交代24口交换机等技术性器件的安装工作","我需要廉租房","北京永和服装饰品有限公司","我爱北京天安门", "结婚的和未婚的","一次性交多少钱","我是中华人民共和国公民;我爸爸是共和党党员; 地铁和平门站,我在深圳车公庙站，福田阳光高尔夫大厦海尔平安金融有限公司，德芙平安金融股份公司","清河县奥尼特羊绒纺织有限公司\n" +
                        "常州市创达热固塑料有限公司\n" +
                        "上海瑞禾房地产发展有限公司\n" +
                        "深圳市中建物资有限公司\n" +
                        "淄博铭宝不锈钢有限公司\n" +
                        "沈阳中圣商贸有限公司\n" +
                        "珠海市德兴达文具有限公司\n" +
                        "广州市金海泰制衣有限公司\n" +
                        "博罗县园洲朗高设计有限公司\n" +
                        "北京惠美利康商贸有限公司\n" +
                        "江苏亿腾化工有限公司\n" +
                        "济南中和本草医药技术有限公司\n" +
                        "宁波洛卡特机电实业有限公司\n" +
                        "洛阳市诚尚印务有限公司\n" +
                        "宁夏广洹工贸有限公司\n" +
                        "苏州茶恩春茶业有限公司\n" +
                        "东海县迅捷贸易有限责任公司" +
                        "東海縣迅捷貿易有限責任公司"};
        for (String sentence : sentences) {
            System.out.println(segmenter.process(sentence, JiebaSegmenter.SegMode.SEARCH).toString());
        }



    }
}
