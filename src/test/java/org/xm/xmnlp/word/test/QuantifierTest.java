package org.xm.xmnlp.word.test;

import org.xm.xmnlp.word.recognition.Quantifier;

/**
 * Created by mingzai on 2016/9/13.
 */
public class QuantifierTest {
    public static void main(String[] args){
        int i =1;
        for(char c: Quantifier.getQuantifier()){
            System.out.println((i++)+" : "+c);
        }
    }
}
