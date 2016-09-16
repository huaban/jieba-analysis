package org.xm.xmnlp.hanlp.seg.NShort;

import org.xm.xmnlp.hanlp.corpus.tag.Nature;
import org.xm.xmnlp.hanlp.utility.Predefine;
import sun.security.provider.certpath.Vertex;

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
                break;
            case Predefine.CT_DELIMITER:
                nature = Nature.w;
                break;
            case Predefine.CT_LETTER:
                nature = Nature.nx;
                sWord = "未##串";
                break;
            case Predefine.CT_SINGLE:
                if (Predefine.PATTERN_FLOAT_NUMBER.matcher(sWord).matches()) {
                    nature = Nature.m;
                    sWord = "未##数";
                } else {
                    nature = Nature.nx;
                    sWord = "未##串";
                }
                break;
            default:
                break;
        }
        return nature;
    }

    @Override
    public String toString() {
        return "AtomNode{" +
                "word='" + sWord + '\'' +
                ", nature=" + nPOS +
                '}';
    }

    public static Vertex convert(String word, int type) {
        String name = word;
        Nature nature = Nature.n;
        int dValue = 1;
        switch (type) {
            case Predefine.CT_CHINESE:
                break;
            case Predefine.CT_INDEX:
            case Predefine.CT_NUM:
                nature = Nature.m;
                word = "未##数";
                break;
            case Predefine.CT_DELIMITER:
                nature = Nature.nx;
                word = "未##串";
                break;
            case Predefine.CT_SINGLE:
                nature = Nature.nx;
                word = "未##串";
                break;
            default:
                break;
        }
        return new Vertex(word, name, new CoreDictionary.Attribute(nature, dValue));
    }
}
