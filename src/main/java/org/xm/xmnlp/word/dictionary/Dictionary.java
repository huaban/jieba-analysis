package org.xm.xmnlp.word.dictionary;

import java.util.List;

/**
 * Created by mingzai on 2016/9/11.
 */
public interface Dictionary {
    int getMaxLength();

    boolean contains(String item);

    boolean contains(String item, int start, int length);

    void addAll(List<String> items);

    void add(String item);

    void removeAll(List<String> items);

    void remove(String item);

    void clear();
}
