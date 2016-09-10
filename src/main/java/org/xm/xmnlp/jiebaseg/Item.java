package org.xm.xmnlp.jiebaseg;

/**
 * Created by mingzai on 2016/9/10.
 */
public class Item<K> {
    public K key;
    public Double freq = 0.0;
    public String nature = "";

    public Item(K key, double freq) {
        this.key = key;
        this.freq = freq;
    }

    public Item(K key, double freq, String nature) {
        this.key = key;
        this.freq = freq;
        this.nature = nature;
    }

//    @Override
//    public String toString() {
//        return "item [key=" + key + ", freq=" + freq + ", nature=" + "]";
//    }
}
