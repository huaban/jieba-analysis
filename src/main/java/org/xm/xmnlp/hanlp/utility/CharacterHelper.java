package org.xm.xmnlp.hanlp.utility;

/**
 * @author xuming
 */
public class CharacterHelper {
    public static boolean isSpaceLetter(char c) {
        return c == 8 || c == 9
                || c == 10 || c == 13
                || c == 32 || c == 160;
    }

    public static boolean isEnglishLetter(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }
}
