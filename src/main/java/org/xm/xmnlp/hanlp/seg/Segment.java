package org.xm.xmnlp.hanlp.seg;

import org.xm.xmnlp.hanlp.dictionary.other.CharType;
import org.xm.xmnlp.hanlp.seg.NShort.AtomNode;

import java.util.LinkedList;
import java.util.List;

/**
 * @author xuming
 */
public abstract class Segment {
    protected Config config;
    public Segment(){
        config = new Config();
    }

    protected static List<AtomNode> quickAtomSegment(char[] charArray, int start, int end){
        List<AtomNode> atomNodeList = new LinkedList<>();
        int offsetAtom= start;
        int preType = CharType.get(charArray[offsetAtom]);
        int curType;
        while(++offsetAtom<end){
            curType = CharType.get(charArray[offsetAtom]);
            if(curType!=preType){
                if(charArray[offsetAtom] == '.' && preType==CharType.CT_NUM){
                    while (++offsetAtom<end){
                        curType = CharType.get(charArray[offsetAtom]);
                        if(curType !=CharType.CT_NUM)break;
                    }
                }
                atomNodeList.add(new AtomNode(new String(charArray ,start,offsetAtom-start),preType));
                start = offsetAtom;
            }
            preType = curType;
        }
        if(offsetAtom == end){
            atomNodeList.add(new AtomNode(new String(charArray,start,offsetAtom - start),preType));
        }
        return atomNodeList;

    }
}

