package org.xm.xmnlp.hanlp.corpus.synonym;

/**
 * @author xuming
 */
public class SynonymHelper {
    public static final long MAX_WORDS = 999L;
    public static final int MAX_INDEX_LENGTH = String.valueOf(MAX_WORDS).length();

    public static long convertString2Id(String idString) {
        long id;
        id = (idString.charAt(0) - 'A') * 26L * 10 * 10 * 26 * 10 * 10 +
                (idString.charAt(1) - 'a') * 10 * 10 * 26 * 10 * 10 +
                (idString.charAt(2) - '0') * 10 * 26 * 10 * 10 +
                (idString.charAt(3) - '0') * 26 * 10 * 10 +
                (idString.charAt(4) - 'A') * 10 * 10 +
                (idString.charAt(5) - '0') * 10 +
                (idString.charAt(6) - '0');    // 编码等号前面的
        return id;
    }

    public static String convertId2String(long id) {
        StringBuilder sbId = new StringBuilder(7);
        sbId.append((char) (id / (26 * 10 * 10 * 26 * 10 * 10) + 'A'));
        sbId.append((char) (id % (26 * 10 * 10 * 26 * 10 * 10) / (10 * 10 * 26 * 10 * 10) + 'a'));
        sbId.append((char) (id % (10 * 10 * 26 * 10 * 10) / (10 * 26 * 10 * 10) + '0'));
        sbId.append((char) (id % (10 * 26 * 10 * 10) / (26 * 10 * 10) + '0'));
        sbId.append((char) (id % (26 * 10 * 10) / (10 * 10) + 'A'));
        sbId.append((char) (id % (10 * 10) / (10) + '0'));
        sbId.append((char) (id % (10) / (1) + '0'));
        return sbId.toString();
    }

    public static long convertString2IdWithIndex(String idString, long index) {
        long id = convertString2Id(idString);
        id = id * MAX_WORDS + index;
        return id;
    }

    public static long convertString2IdWithIndex(String idString, int index) {
        return convertString2IdWithIndex(idString, (long) index);
    }

    public static String convertId2StringWithIndex(long id) {
        String idString = convertId2String(id / MAX_WORDS);
        long index = id % MAX_WORDS;
        return String.format("%s%0" + MAX_INDEX_LENGTH + "d", idString, index);
    }

    public static void main(String[] args) {
        System.out.println(convertId2StringWithIndex(convertString2IdWithIndex("Zg83H75=", 0)));
    }

}
