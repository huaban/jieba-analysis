package org.xm.xmnlp.word.demo;

import org.xm.xmnlp.word.segmentation.Segmentation;
import org.xm.xmnlp.word.segmentation.impl.PureEnglish;

/**
 * Created by xuming
 */
public class PureEnglishDemo {
    public static void main(String[] args){
        Segmentation seg = new PureEnglish();
        System.out.print(seg.seg("Your fucntion is also added permanently to" +
                " a new one , i need add 1+2=3.0 to 4 answer is 7.0," +
                " however this requires a small modifiction to Hive a Java " +
                "fiction. Think you!<br> xx "));
    }
}
