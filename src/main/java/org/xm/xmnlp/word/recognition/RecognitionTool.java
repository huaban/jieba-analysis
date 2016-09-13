package org.xm.xmnlp.word.recognition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xm.xmnlp.word.util.WordConfTools;


/**
 * Created by mingzai on 2016/9/13.
 */
public class RecognitionTool {
    private static final Logger LOGGER = LoggerFactory.getLogger(RecognitionTool.class);
    private static final boolean RECOGNITION_TOOL_ENABLED = WordConfTools.getBoolean("recognition.tool.enabled", true);
    private static final char[] chineseNumbers = {'一', '二', '三', '四', '五', '六', '七', '八', '九', '十', '百', '千', '万', '亿', '零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖', '拾', '佰', '仟', '〇'};

    public static boolean recog(final String text) {
        return recog(text, 0, text.length());
    }

    public static boolean recog(String text, int start, int len) {
        if (!RECOGNITION_TOOL_ENABLED) {
            return false;
        }
        return isEnglishAndNumberMix(text, start, len)
                || isFraction(text, start, len)
                || isQuantifier(text, start, len)
                || isChineseNumber(text, start, len);
    }

    private static boolean isEnglishAndNumberMix(String text, int start, int len) {
        for (int i = start; i < start + len; i++) {
            char c = text.charAt(i);
            if (!(isEnglish(c) || isNumber(c))) {
                return false;
            }
        }
        if (start > 0) {
            char c = text.charAt(start - 1);
            if (isEnglish(c) || isNumber(c)) {
                return false;
            }
        }
        if (start + len < text.length()) {
            char c = text.charAt(start + len);
            if (isEnglish(c) || isNumber(c)) {
                return false;
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("识别出英文字母和数字混合串：" + text.substring(start, start + len));
        }
        return true;
    }

    public static boolean isEnglish(char c) {
        if (c > 'z' && c < 'Ａ') {
            return false;
        }
        if (c < 'A') {
            return false;
        }
        if (c > 'Z' && c < 'a') {
            return false;
        }
        if (c > 'Ｚ' && c < 'ａ') {
            return false;
        }
        if (c > 'ｚ') {
            return false;
        }
        return true;
    }

    public static boolean isEnglish(final String text) {
        return isEnglish(text, 0, text.length());
    }

    public static boolean isEnglish(String text, int start, int len) {
        for (int i = start; i < start + len; i++) {
            char c = text.charAt(i);
            if (!isEnglish(c)) {
                return false;
            }
        }
        if (start > 0) {
            char c = text.charAt(start - 1);
            if (isEnglish(c)) {
                return false;
            }
        }
        if (start + len < text.length()) {
            char c = text.charAt(start + len);
            if (isEnglish(c)) {
                return false;
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("recognize english word:" + text.substring(start, start + len));
        }
        return true;

    }

    public static boolean isFraction(final String text) {
        return isFraction(text, 0, text.length());
    }

    public static boolean isFraction(final String text, final int start, final int length) {
        if (length < 3) {
            return false;
        }
        int index = -1;
        for (int i = start; i < start + length; i++) {
            char c = text.charAt(i);
            if (c == '.' || c == '/' || c == '／' || c == '．' || c == '·') {
                index = i;
                break;
            }
        }
        if (index == -1 || index == start || index == start + length - 1) {
            return false;
        }
        int beforeLen = index - start;
        return isNumber(text, start, beforeLen) && isNumber(text, index + 1, length - (beforeLen + 1));
    }

    public static boolean isNumber(final String text) {
        return isNumber(text, 0, text.length());
    }

    public static boolean isNumber(final String text, final int start, final int len) {
        for (int i = start; i < start + len; i++) {
            char c = text.charAt(i);
            if (!isNumber(c)) {
                return false;
            }
        }
        if (start > 0) {
            char c = text.charAt(start - 1);
            if (isNumber(c)) {
                return false;
            }
        }
        if (start + len < text.length()) {
            char c = text.charAt(start + len);
            if (isNumber(c)) {
                return false;
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("found num:" + text.substring(start, start + len));
        }
        return true;
    }

    public static boolean isNumber(char c) {
        if (c > '9' && c < '０') {
            return false;
        }
        if (c < '0') {
            return false;
        }
        if (c > '９') {
            return false;
        }
        return true;
    }

    public static boolean isChineseNumber(final String text) {
        return isChineseNumber(text, 0, text.length());
    }

    public static boolean isChineseNumber(String text, int start, int len) {
        for (int i = start; i < start + len; i++) {
            char c = text.charAt(i);
            boolean isChineseNumber = false;
            for (char chineseNumber : chineseNumbers) {
                if (c == chineseNumber) {
                    isChineseNumber = true;
                    break;
                }
            }
            if (!isChineseNumber) {
                return false;
            }
        }
        if (start > 0) {
            char c = text.charAt(start - 1);
            for (char chineseNumber : chineseNumbers) {
                if (c == chineseNumber) {
                    return false;
                }
            }
        }
        if (start + len < text.length()) {
            char c = text.charAt(start + len);
            for (char chineseNumber : chineseNumbers) {
                if (c == chineseNumber) {
                    return false;
                }
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("recognize chinese num:" + text.substring(start, start + len));
        }
        return true;
    }

    public static boolean isQuantifier(final String text) {
        return isQuantifier(text, 0, text.length());
    }

    public static boolean isQuantifier(final String text, final int start, final int len) {
        if (len < 2) {
            return false;
        }
        int index = start - 1;
        if (index > -1 && (text.charAt(index) == 46 || text.charAt(index) == 47)) {
            return false;
        }
        char lastChar = text.charAt(start + len - 1);
        if (Quantifier.isQuantifier(lastChar)
                && (isNumber(text, start, len - 1)
                || isChineseNumber(text, start, len - 1)
                || isFraction(text, start, len - 1))) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("recgnize num quantifier words:" + text.substring(start, start + len));
            }
            return true;
        }
        return false;
    }


    public static void main(String[] args) {
        String i = "1.08%";
        LOGGER.info("" + recog(i, 0, i.length()));
    }
}
