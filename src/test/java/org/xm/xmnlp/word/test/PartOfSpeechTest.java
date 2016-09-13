package org.xm.xmnlp.word.test;

import org.xm.xmnlp.word.segmentation.PartOfSpeech;

/**
 * Created by xuming
 */
public class PartOfSpeechTest {
    public static void main(String[] args) {
        System.out.println(PartOfSpeech.isPos("n"));
        System.out.println(PartOfSpeech.isPos("ns"));
        System.out.println(PartOfSpeech.I.getPos() + " " + PartOfSpeech.I.getDes());
        PartOfSpeech N_ANIMAL = new PartOfSpeech("n_animal", "动物user");
        System.out.println(N_ANIMAL.getPos() + " " + N_ANIMAL.getDes());
    }
}
