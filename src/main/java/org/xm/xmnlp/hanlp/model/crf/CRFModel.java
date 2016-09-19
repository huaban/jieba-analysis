package org.xm.xmnlp.hanlp.model.crf;

import org.xm.xmnlp.hanlp.collection.trie.DoubleArrayTrie;
import org.xm.xmnlp.hanlp.collection.trie.ITrie;
import org.xm.xmnlp.hanlp.corpus.io.ByteArray;
import org.xm.xmnlp.hanlp.corpus.io.ICacheAble;

import java.io.DataOutputStream;
import java.util.List;
import java.util.Map;

/**
 * @author xuming
 */
public class CRFModel implements ICacheAble {
    Map<String,Integer> tag2id;
    protected String[] id2tag;
    ITrie<FeatureFunction> featureFunctionTrie;
    List<FeatureTemplate> featureTemplateList;
    protected double[][] matrix;
    public CRFModel(){
        featureFunctionTrie = new DoubleArrayTrie<FeatureFunction>();
    }

    @Override
    public void save(DataOutputStream out) throws Exception {

    }

    @Override
    public boolean load(ByteArray byteArray) {
        return false;
    }
}
