package org.xm.xmnlp.hanlp.model.crf;

import org.xm.xmnlp.hanlp.corpus.io.ByteArray;
import org.xm.xmnlp.hanlp.corpus.io.ICacheAble;

import java.io.DataOutputStream;

/**
 * @author xuming
 */
public class FeatureFunction implements ICacheAble {
    @Override
    public void save(DataOutputStream out) throws Exception {

    }

    @Override
    public boolean load(ByteArray byteArray) {
        return false;
    }
}
