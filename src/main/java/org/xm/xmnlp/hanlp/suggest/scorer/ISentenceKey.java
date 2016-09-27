package org.xm.xmnlp.hanlp.suggest.scorer;

/**
 * 可以唯一代表一个句子的键，可以与其他句子区别开来
 * @author xuming
 */
public interface ISentenceKey<T> {
    Double similarity(T other);
}
