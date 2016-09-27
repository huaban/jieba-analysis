package org.xm.xmnlp.hanlp.algorithm;

/**
 * @author xuming
 */
public class ArrayCompare {
    public static int compare(Long[] arrayA, Long[] arrayB) {
        int len1 = arrayA.length;
        int len2 = arrayB.length;
        int min = Math.min(len1, len2);
        int k = 0;
        while (k < min) {
            Long c1 = arrayA[k];
            Long c2 = arrayB[k];
            if (!c1.equals(c2)) {
                return c1.compareTo(c2);
            }
            ++k;
        }
        return len1 - len2;
    }
}
