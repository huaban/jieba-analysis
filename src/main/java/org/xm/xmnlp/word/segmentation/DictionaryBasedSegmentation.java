package org.xm.xmnlp.word.segmentation;


import org.xm.xmnlp.word.dictionary.Dictionary;

/**
 * Created by xuming
 */
public interface DictionaryBasedSegmentation  extends Segmentation{
    void setDictionary(Dictionary dictionary);

    Dictionary getDictionary();
}
