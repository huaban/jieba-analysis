package org.xm.xmnlp.hanlp.model.crf;

import org.xm.xmnlp.hanlp.corpus.io.ByteArray;
import org.xm.xmnlp.hanlp.corpus.io.ICacheAble;

import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author xuming
 */
public class FeatureTemplate implements ICacheAble {
    static final Pattern pattern = Pattern.compile("%x\\[(-?\\d*),(\\d*)]");
    String template;
    ArrayList<int[]> offsetList;
    List<String> delimiterList;
    @Override
    public void save(DataOutputStream out) throws Exception {

    }

    @Override
    public boolean load(ByteArray byteArray) {
        return false;
    }
}
