package org.xm.xmnlp.word.test;

import org.xm.xmnlp.word.recognition.StopWord;

import java.util.Set;

/**
 * Created by xuming
 */
public class StopWordTest {
    public static void main(String[] args) {
        Set<String> stopwords =StopWord.getStopwords();
        System.out.println("stop word:");
        int i = 1;
        for (String w : stopwords) {
            System.out.println((i++) + ": " + w);
        }
    }
    
}
