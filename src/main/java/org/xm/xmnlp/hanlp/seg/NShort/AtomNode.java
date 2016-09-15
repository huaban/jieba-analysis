package org.xm.xmnlp.hanlp.seg.NShort;

import org.xm.xmnlp.hanlp.corpus.tag.Nature;
import org.xm.xmnlp.hanlp.utility.Predefine;

/**
 * @author xuming
 */
public class AtomNode {
    public String sWord;
    public int nPOS;

    public AtomNode(String sWord, int nPOS) {
        this.sWord = sWord;
        this.nPOS = nPOS;
    }

    public AtomNode(char c, int nPOS) {
        this.sWord = String.valueOf(c);
        this.nPOS = nPOS;
    }

    public Nature getNature() {
        Nature nature = Nature.nz;
        switch (nPOS) {
            case Predefine.CT_CHINESE:
                break;
            case Predefine.CT_INDEX:
            case Predefine.CT_NUM:
                nature = Nature.m;
                sWord = "未##数";
        }
    }
}
