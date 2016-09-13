package org.xm.xmnlp.word.test;

import static org.xm.xmnlp.word.recognition.RecognitionTool.recog;

/**
 * Created by mingzai on 2016/9/13.
 */
public class RecognitionToolTest {
    public static void main(String[] args) {
        String i = "1.08%";
        System.out.println("" + recog(i, 0, i.length()));
    }
}
