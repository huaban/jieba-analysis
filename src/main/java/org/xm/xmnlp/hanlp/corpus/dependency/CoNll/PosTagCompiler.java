package org.xm.xmnlp.hanlp.corpus.dependency.CoNll;

import org.xm.xmnlp.hanlp.utility.Predefine;

/**
 * @author xuming
 */
public class PosTagCompiler {
    public static String compile(String tag, String name) {
        if (tag.startsWith("m")) return Predefine.TAG_NUMBER;
        else if (tag.startsWith("nr")) return Predefine.TAG_PEOPLE;
        else if (tag.startsWith("ns")) return Predefine.TAG_PLACE;
        else if (tag.startsWith("nt")) return Predefine.TAG_GROUP;
        else if (tag.startsWith("t")) return Predefine.TAG_TIME;
        else if (tag.equals("x")) return Predefine.TAG_CLUSTER;
        else if (tag.equals("nx")) return Predefine.TAG_PROPER;
        else if (tag.equals("xx")) return Predefine.TAG_OTHER;
        return name;
    }
}
