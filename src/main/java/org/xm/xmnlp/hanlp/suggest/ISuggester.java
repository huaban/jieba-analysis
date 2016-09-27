package org.xm.xmnlp.hanlp.suggest;

import java.util.List;

/**
 * @author xuming
 */
public interface ISuggester {
    void addSentence(String sentence);
    void removeAllSentences();
    List<String> suggest(String key,int size);
}
