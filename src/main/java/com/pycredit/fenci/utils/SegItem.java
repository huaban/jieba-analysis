package com.pycredit.fenci.utils;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by xuming on 2016/6/2.
 */


public class SegItem extends Item implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final SegItem NULL = new SegItem();

    public static final SegItem BEGIN = new SegItem();

    public static final SegItem END = new SegItem();

    static {
        NULL.base = 0;

        BEGIN.index = 0;
//        BEGIN.termNatures = TermNatures.BEGIN;

        END.index = -1;
//        END.termNatures = TermNatures.END;
    }

    public String param;

    /**
     * frequency : 词性词典,以及词性的相关权重
     */
//    public TermNatures termNatures = null ;

    public Map<Integer,Integer> bigramEntryMap =  null ;

    @Override
    public void init(String[] split) {
        this.name = split[0];
        this.param = split[1];
    }

    @Override
    public void initValue(String[] split) {
        index = Integer.parseInt(split[0]);
        base = Integer.parseInt(split[2]);
        check = Integer.parseInt(split[3]);
        status = Byte.parseByte(split[4]);
        if (status > 1) {
            name = split[1];
//            termNatures = new TermNatures(TermNature.setNatureStrToArray(split[5]), index);
        }else{
//            termNatures = new TermNatures(TermNature.NULL);
        }
    }

    @Override
    public String toText() {
        return index + "\t" + name + "\t" + base + "\t" + check + "\t" + status + "\t" + param;
    }

}
