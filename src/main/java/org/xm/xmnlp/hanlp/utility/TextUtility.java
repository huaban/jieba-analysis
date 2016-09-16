package org.xm.xmnlp.hanlp.utility;

import java.io.*;
import java.util.Collection;

/**
 * @author xuming
 */
public class TextUtility {
    public static final int CT_SINGLE = 5;
    public static final int CT_DELIMITER = CT_SINGLE + 1;
    public static final int CT_CHINESE = CT_SINGLE + 2;
    public static final int CT_LETTER = CT_SINGLE + 3;
    public static final int CT_NUM = CT_SINGLE + 4;
    public static final int CT_INDEX = CT_SINGLE + 5;
    public static final int CT_OTHER = CT_SINGLE + 12;

    public static int charType(char c) {
        return charType(String.valueOf(c));
    }

    public static int charType(String str) {
        if (str != null && str.length() > 0) {
            if ("零○〇一二两三四五六七八九十廿百千万亿壹贰叁肆伍陆柒捌玖拾佰仟".contains(str)) {
                return CT_NUM;
            }
            byte[] b;
            try {
                b = str.getBytes("GBK");
            } catch (UnsupportedEncodingException e) {
                b = str.getBytes();
                e.printStackTrace();
            }
            byte b1 = b[0];
            byte b2 = b.length > 1 ? b[1] : 0;
            int ub1 = getUnsigned(b1);
            int ub2 = getUnsigned(b2);
            if (ub1 < 128) {
                if (' ' == b1) return CT_OTHER;
                if ('\n' == b1) return CT_DELIMITER;
                if ("*\"!,.?(){}+=/\\;:|".indexOf((char) b1) != -1) return CT_DELIMITER;
                if ("0123456789".indexOf((char) b1) != -1) return CT_NUM;
                return CT_SINGLE;
            } else if (ub1 == 162) return CT_INDEX;
            else if (ub1 == 163 && ub2 > 175 && ub2 < 186) return CT_NUM;
            else if (ub1 == 163
                    && (ub2 >= 193 && ub2 <= 218 || ub2 > 225
                    && ub2 < 250)) return CT_LETTER;
            else if (ub1 == 161 || ub1 == 163) return CT_DELIMITER;
            else if (ub1 > 176 && ub1 <= 247) return CT_CHINESE;
        }
        return CT_OTHER;
    }

    public static boolean isAllChinese(String str) {
        return str.matches("[\\u4E00-\\u9FA5]+");
    }

    public static boolean isAllNonChinese(byte[] sString) {
        int nLen = sString.length;
        int i = 0;
        while (i < nLen) {
            if (getUnsigned(sString[i]) < 248 && getUnsigned(sString[i]) > 175) {
                return false;
            }
            if (sString[i] < 0) {
                i += 2;
            } else {
                i += 1;
            }
        }
        return true;
    }

    public static boolean isAllSingleByte(String str) {
        if (str != null) {
            int len = str.length();
            int i = 0;
            byte[] b;
            try {
                b = str.getBytes("GBK");
            } catch (UnsupportedEncodingException e) {
                b = str.getBytes();
                e.printStackTrace();

            }
            while (i < len && b[i] < 128) {
                i++;
            }
            if (i < len) {
                return false;
            }
            return true;
        }
        return false;
    }

    public static int cint(String str) {
        if (str != null) {
            try {
                int i = new Integer(str).intValue();
                return i;
            } catch (NumberFormatException e) {

            }
        }
        return -1;
    }

    public static boolean isAllNum(String str) {
        if (str != null) {
            int i = 0;
            String temp = str + " ";
            if ("±+—-＋".indexOf(temp.substring(0, 1)) != -1) {
                i++;
            }
            while (i < str.length() && "０１２３４５６７８９".indexOf(str.substring(i, i + 1)) != -1) {
                i++;
            }
            if (i < str.length()) {
                String s = str.substring(i, i + 1);
                if ("∶·．／".indexOf(s) != -1 || ".".equals(s) || "/".equals(s)) {
                    i++;
                    while (i + 1 < str.length() && "０１２３４５６７８９".indexOf(str.substring(i + 1, i + 2)) != -1) {
                        i++;
                    }
                }

            }
            if (i >= str.length()) return true;
            while (i < str.length() && cint(str.substring(i, i + 1)) >= 0
                    && cint(str.substring(i, i + 1)) <= 9) {
                i++;
            }

            if (i < str.length()) {
                String s = str.substring(i, i + 1);
                if ("∶·．／".indexOf(s) != -1 || ".".equals(s) || "/".equals(s)) {
                    i++;
                    while (i + 1 < str.length() && "0123456789".indexOf(str.substring(i + 1, i + 2)) != -1) {
                        i++;
                    }
                }
            }

            if (i < str.length()) {
                if ("百千万亿佰仟％‰".indexOf(str.substring(i, i + 1)) == -1
                        && !"%".equals(str.substring(i, i + 1))) {
                    i--;
                }
            }

            if (i >= str.length()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAllLetter(String text) {
        for (int i = 0; i < text.length(); ++i) {
            char c = text.charAt(i);
            if ((c < 'a' || c > 'z') && (c < 'A' || c > 'Z')) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAllLetterOrNum(String text) {
        for (int i = 0; i < text.length(); ++i) {
            char c = text.charAt(i);
            if ((c < 'a' || c > 'z') && (c < 'A' || c > 'Z') && (c < '0' || c > '9')) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAllDelimiter(byte[] sString) {
        int nLen = sString.length;
        int i = 0;
        while (i < nLen - 1 && (getUnsigned(sString[i]) == 161 || getUnsigned(sString[i]) == 163)) {
            i += 2;
        }
        if (i < nLen) {
            return false;
        }
        return true;
    }

    public static boolean isAllChineseNum(String word) {
        String chineseNum = "零○一二两三四五六七八九十廿百千万亿壹贰叁肆伍陆柒捌玖拾佰仟∶·．／点";
        String prefix = "几数第上成";
        if (word != null) {
            String temp = word + " ";
            for (int i = 0; i < word.length(); i++) {
                if (temp.indexOf("分之", i) != -1) {
                    i += 2;
                    continue;
                }
                String tchar = temp.substring(i, i + 1);
                if (chineseNum.indexOf(tchar) == -1 && (i != 0 || prefix.indexOf(tchar) == -1)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static int getCharCount(String charSet, String word) {
        int nCount = 0;
        if (word != null) {
            String temp = word + " ";
            for (int i = 0; i < word.length(); i++) {
                String s = temp.substring(i, i + 1);
                if (charSet.indexOf(s) != -1) {
                    nCount++;
                }
            }
        }
        return nCount;
    }

    public static int getUnsigned(byte b) {
        if (b > 0) {
            return (int) b;
        } else {
            return (b & 0x7F + 128);
        }
    }

    public static boolean isYearTime(String snum) {
        if (snum != null) {
            int len = snum.length();
            String first = snum.substring(0, 1);
            if (isAllSingleByte(snum) && (len == 4 || len == 2 && (cint(first) > 4 || cint(first) == 0))) {
                return true;
            }
            if (isAllNum(snum) && (len >= 6 || len == 4 && "０５６７８９".indexOf(first) != -1)) {
                return true;
            }
            if (getCharCount("零○一二三四五六七八九壹贰叁肆伍陆柒捌玖", snum) == len && len >= 2) {
                return true;
            }
            if (len == 4 && getCharCount("千仟零○", snum) == 2) {
                return true;
            }
            if (len == 1 && getCharCount("千仟", snum) == 1) {
                return true;
            }
            if (len == 2 && getCharCount("甲乙丙丁戊己庚辛壬癸", snum) == 1
                    && getCharCount("子丑寅卯辰巳午未申酉戌亥", snum.substring(1)) == 1) {
                return true;
            }
        }
        return false;
    }

    public static boolean isInAggregate(String aggr, String str) {
        if (aggr != null && str != null) {
            str += "1";
            for (int i = 0; i < str.length(); i++) {
                String s = str.substring(i, i + 1);
                if (aggr.indexOf(s) == -1) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static boolean isDBCCase(String str) {
        if (str != null) {
            str += " ";
            for (int i = 0; i < str.length(); i++) {
                String s = str.substring(i, i + 1);
                int length = 0;
                try {
                    length = s.getBytes("GBK").length;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    length = s.getBytes().length;
                }
                if (length != 1) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static boolean isSBCCase(String str) {
        if (str != null) {
            str += " ";
            for (int i = 0; i < str.length(); i++) {
                String s = str.substring(i, i + 1);
                int length = 0;
                try {
                    length = s.getBytes("GBK").length;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    length = s.getBytes().length;
                }
                if (length != 2) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static boolean isDelimiter(String str) {
        if (str != null && ("-".equals(str) || "－".equals(str))) {
            return true;
        } else {
            return false;
        }
    }

    public static double nonZero(double frequency) {
        if (frequency == 0) return 1e-3;
        return frequency;
    }

    public static char[] long2char(long x) {
        char[] c = new char[4];
        c[0] = (char) (x >> 48);
        c[1] = (char) (x >> 32);
        c[2] = (char) (x >> 16);
        c[3] = (char) (x);
        return c;
    }

    public static String exceptionToString(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    public static boolean isChinese(char c) {
        String regex = "[\\u4e00-\\u9fa5]";
        return String.valueOf(c).matches(regex);
    }

    public static int count(String keyword, String srcText) {
        int count = 0;
        int len = srcText.length();
        int j = 0;
        for (int i = 0; i < len; i++) {
            if (srcText.charAt(i) == keyword.charAt(j)) {
                j++;
                if (j == keyword.length()) {
                    count++;
                    j = 0;
                }
            } else {
                i = i - j;
                j = 0;
            }
        }
        return count;
    }

    public static void writeString(String s, DataOutputStream out) throws IOException {
        out.writeInt(s.length());
        for (char c : s.toCharArray()) {
            out.writeChar(c);
        }
    }

    public static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static String join(String delimiter, Collection<String> stringCollection) {
        StringBuilder sb = new StringBuilder(stringCollection.size() * (16 + delimiter.length()));
        for (String str : stringCollection) {
            sb.append(str).append(delimiter);
        }
        return sb.toString();
    }


}
