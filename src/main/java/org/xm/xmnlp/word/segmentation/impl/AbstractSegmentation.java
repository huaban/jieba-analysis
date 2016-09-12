package org.xm.xmnlp.word.segmentation.impl;

import org.xm.xmnlp.word.dictionary.Dictionary;
import org.xm.xmnlp.word.segmentation.DictionaryBasedSegmentation;

/**
 * Created by xuming
 */
public class AbstractSegmentation implements DictionaryBasedSegmentation {

    @Override
    public void setDictionary(Dictionary dictionary) {

    }

    @Override
    public Dictionary getDictionary() {
        return null;
    }
}
