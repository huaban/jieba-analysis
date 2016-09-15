package org.xm.xmnlp.hanlp.seg;

import java.util.List;

/**
 * @author xuming
 */
public abstract class Segment {
    protected Config config;
    public Segment(){
        config = new Config();
    }

    protected static List<AtomNode> quickAtomSegment(char[] charArray,int start,int end){
        List<AtomNode> = start;

    }
}
