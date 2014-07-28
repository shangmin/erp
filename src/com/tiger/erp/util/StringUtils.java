/*
 * TextProcessor.java
 *
 */

package com.tiger.erp.util;

import java.util.Collections;
import java.util.Vector;
import java.util.Locale;
import java.util.StringTokenizer;
import java.text.SimpleDateFormat;
import java.text.DecimalFormat;


public class StringUtils {

    public static String EMPTY_STR = "";
    public static String COLON = ":";

    /** Creates new TextProcessor */
    private StringUtils() {
    }

    public static byte[] hexToBytes(String hexStr) throws Exception {

        if (hexStr == null)
            return null;

        if (hexStr.length() % 2 != 0)
            throw new Exception("Length of data is not equal to even number");

        byte[] rtnBytes = new byte[hexStr.length() / 2];

        for (int i = 0; i < hexStr.length() / 2; i++)
            rtnBytes[i] = (byte) Integer.parseInt(hexStr.substring(i * 2, i * 2 + 2), 16);

        return rtnBytes;
    }

    public static boolean equals(char a, char b) {
        return Character.toUpperCase(a) == Character.toUpperCase(b);
    }

    public static boolean equals(String a, String b) {
        if (a == null || b == null) {
            return false;
        }
        return a.equals(b);
    }

    /**
     * Convert a long value in millisec to hh:MM:ss
     */
    public static String longToHHMMSS(long someTime) {

        long totalSec = someTime / 1000;
        long sec = totalSec % 60;

        String ss = leftPad(Long.toString(sec), 2, '0');

        long min = ((totalSec - sec) / 60) % 60;
        String mm = leftPad(Long.toString(min), 2, '0');

        String hh = Long.toString((totalSec - sec) / 3600);

        return new StringBuffer().append(hh).append(':').append(mm).append(':').append(ss).toString();
    }

    public static String leftPad(String inStr, int length, char c) {
        if (inStr.length() == length)
            return inStr;

        StringBuffer outStr = new StringBuffer();
        for (int i = inStr.length(); i < length; i++) {
            outStr.append(c);
        }
        outStr.append(inStr);

        return outStr.toString();
    }

    public static String rightPad(String inStr, int length, char c) {
        if (inStr.length() == length)
            return inStr;

        StringBuffer outStr = new StringBuffer();
        outStr.append(inStr);

        for (int i = inStr.length(); i < length; i++) {
            outStr.append(c);
        }

        return outStr.toString();
    }

    /** Tokenizes a given string according to the specified delimiters. The characters in the delim argument are the delimiters for separating tokens. Delimiter characters themselves will not be treated as tokens.
     * @param str A string to be parsed.
     * @param delim The delimiters.
     * @return The tokens in a String array.
     */
    public static String[] tokenize(String str, String delim) {
        String[] strs = null;
        if (str != null) {
            StringTokenizer tokens;
            if (delim == null)
                tokens = new StringTokenizer(str);
            else
                tokens = new StringTokenizer(str, delim);
            strs = new String[tokens.countTokens()];
            for (int i = 0; i < strs.length && tokens.hasMoreTokens(); i++) {
                strs[i] = tokens.nextToken();
            }
        }
        return strs;
    }

    /** Tokenizes a given string according to a fixed length. If the last token's length is less than the fixed length specified, it will be ignored.
     * @param str A string to be parsed.
     * @param fixedLength The fixed length.
     * @return The tokens in a String array.
     */
    public static String[] tokenize(String str, int fixedLength) {
        String[] strs = null;
        if (str != null && fixedLength > 0) {
            Vector v = new Vector();
            for (int i = 0; i < str.length(); i += fixedLength) {
                int next = i + fixedLength;
                if (next > str.length())
                    next = str.length();
                v.addElement(str.substring(i, next));
            }
            strs = (String[]) v.toArray(new String[] {});
        }
        return strs;
    }

    /** Concatenates a String array (String tokens) into a String with the specified delimiter String.
     * @param tokens A String array to be concatenated.
     * @param delim The delimiter.
     * @return The concatenated String.
     */
    public static String concat(String[] tokens, String delim) {
        return concat(tokens, EMPTY_STR, EMPTY_STR, delim);
    }

    /** Concatenates a String array (String tokens) into a String with the specified delimiter String, token's prefix, and token's suffix.
     * @param tokens A String array to be concatenated.
     * @param tokenPrefix The token's prefix to be concatenated.
     * @param tokenSuffix The token's suffix to be concatenated.
     * @param delim The delimiter.
     * @return The concatenated String.
     */
    public static String concat(String[] tokens, String tokenPrefix, String tokenSuffix, String delim) {
        StringBuffer s = new StringBuffer();
        if (tokens != null) {
            if (tokenPrefix == null)
                tokenPrefix = EMPTY_STR;
            if (tokenSuffix == null)
                tokenSuffix = EMPTY_STR;
            if (delim == null)
                delim = EMPTY_STR;
            for (int i = 0; i < tokens.length; i++) {
                //s += tokenPrefix+tokens[i]+tokenSuffix;
                //if (i+1<tokens.length) s += delim;
                s.append(tokenPrefix).append(tokens[i]).append(tokenSuffix);
                if (i + 1 < tokens.length)
                    s.append(delim);
            }
        }
        return s.toString();
    }

    /** Checks if a given String array contains the specified search String.
     * @param tokens A String array to be searched.
     * @param target The target search String.
     * @return <PRE>true</PRE> if the given String array contains the specified search String, <PRE>false</PRE> otherwise.
     */
    public static boolean contains(String[] tokens, String target) {
        if (tokens != null) {
            for (int i = 0; i < tokens.length; i++) {
                if (tokens[i] == null) {
                    if (target == null)
                        return true;
                } else {
                    if (tokens[i].equals(target))
                        return true;
                }
            }

        }
        return false;
    }

    /** Repeats a given String in the specified number of times, then concatenates and returns it.
     * @param s A String to be repeated and concatenated.
     * @param occurs The number of times of the given String to be repeated.
     * @return The concatenated String.
     */
    public static String repeatString(String s, int occurs) {
        StringBuffer result = new StringBuffer();
        if (s != null && occurs > 0) {
            for (int i = 0; i < occurs; i++) {
                result.append(s);
            }
        }
        return result.toString();
    }

    /** Checks if a given String contains only digits.
     * @param s A String to be checked.
     * @return <PRE>true</PRE> if the given String contains only digits, <PRE>false</PRE> otherwise.
     */
    public static boolean isAllDigit(String s) {
        if (s == null || s.equals(EMPTY_STR))
            return false;
        else {
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (!Character.isDigit(c))
                    return false;
            }
            return true;
        }
    }

    public static boolean containsAlphabets(String s) {
        if (s == null || s.equals(EMPTY_STR))
            return false;
        else {
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                return true;
            }
            return false;
        }
    }

    /** Formats a given double into a String according to the specified pattern.
     * @param d The double to be formatted.
     * @param pattern The pattern to be followed in formatting.
     * @return The formatted String of the double.
     */
    public static String formatDecimal(double d, String pattern) {
        return new DecimalFormat(pattern).format(d);
    }

    /** Formats a given java.util.Date into a String according to the specified pattern.
     * @param date The date to be formatted.
     * @param pattern The pattern to be followed in formatting.
     * @return The formatted String of the date.
     */
    public static String formatDate(java.util.Date date, String pattern) {
        return formatDate(date, pattern, null);
    }

    /** Formats a given java.util.Date into a String according to the specified pattern.
     * @return The formatted String of the date.
     * @param locale The locale used in formatting.
     * @param date The date to be formatted.
     * @param pattern The pattern to be followed in formatting.
     */
    public static String formatDate(java.util.Date date, String pattern, Locale locale) {
        try {
            if (locale == null)
                return new SimpleDateFormat(pattern).format(date);
            else
                return new SimpleDateFormat(pattern, locale).format(date);
        } catch (Exception e) {
            return date.toString();
        }
    }

    /** Parses a date string and returns a java.util.Date object.
     * @param date The date string to be parsed.
     * @param pattern The pattern of the date string.
     * @return A java.util.Date object that represents the given date string.
     */
    public static java.util.Date parseDate(String date, String pattern) {
        return parseDate(date, pattern, null);
    }

    /** Parses a date string and returns a java.util.Date object.
     * @param date The date string to be parsed.
     * @param pattern The pattern of the date string.
     * @param locale The locale used in parsing the date string.
     * @return A java.util.Date object that represents the given date string.
     */
    public static java.util.Date parseDate(String date, String pattern, Locale locale) {
        try {
            SimpleDateFormat dateFormatter;
            if (locale == null)
                dateFormatter = new SimpleDateFormat(pattern);
            else
                dateFormatter = new SimpleDateFormat(pattern, locale);
            dateFormatter.setLenient(false);
            return dateFormatter.parse(date);
        } catch (Exception e) {
            return null;
        }
    }

    /** Adds leading zeros to the given String to the specified length. Nothing will be done if the length of the given String is equal to or greater than the specified length.
     * @param s The source string.
     * @param len The length of the target string.
     * @return The String after adding leading zeros.
     */
    public static String addLeadingZero(String s, int len) {
        return addLeadingCharacter(s, '0', len);
    }

    /** Adds leading spaces to the given String to the specified length. Nothing will be done if the length of the given String is equal to or greater than the specified length.
     * @param s The source string.
     * @param len The length of the target string.
     * @return The String after adding leading spaces.
     */
    public static String addLeadingSpace(String s, int len) {
        return addLeadingCharacter(s, ' ', len);
    }

    /** Adds specified leading characters to the specified length. Nothing will be done if the length of the given String is equal to or greater than the specified length.
     * @param s The source string.
     * @param c The leading character(s) to be added.
     * @param len The length of the target string.
     * @return The String after adding the specified leading character(s).
     */
    public static String addLeadingCharacter(String s, char c, int len) {
        if (s != null) {
            StringBuffer sb = new StringBuffer();
            int count = len - s.length();
            for (int i = 0; i < count; i++) {
                sb.append(c);
            }
            sb.append(s);
            return sb.toString();
        } else {
            return null;
        }
    }

    /** Removes leading zeros from the given String, if any.
     * @param s The source string.
     * @return The String after removing leading zeros.
     */
    public static String removeLeadingZero(String s) {
        return removeLeadingCharacter(s, '0');
    }

    /** Removes leading spaces from the given String, if any.
     * @param s The source string.
     * @return The String after removing leading spaces.
     */
    public static String removeLeadingSpace(String s) {
        return removeLeadingCharacter(s, ' ');
    }

    /** Removes specified leading characters from the given String, if any.
     * @param s The source string.
     * @param c The leading character(s) to be removed.
     * @return The String after removing the specified leading character(s).
     */
    public static String removeLeadingCharacter(String s, char c) {
        if (s != null) {
            int len = s.length();
            int i = 0;
            for (i = 0; i < len; i++) {
                if (s.charAt(i) != c) {
                    break;
                }
            }
            if (i > 0) {
                return s.substring(i);
            } else {
                return s;
            }

        } else {
            return null;
        }
    }

    /** Appends zeros to the given String to the specified length. Nothing will be done if the length of the given String is equal to or greater than the specified length.
     * @param s The source string.
     * @param len The length of the target string.
     * @return The String after appending zeros.
     */
    public static String appendZero(String s, int len) {
        return appendCharacter(s, '0', len);
    }

    /** Appends spaces to the given String to the specified length. Nothing will be done if the length of the given String is equal to or greater than the specified length.
     * @param s The source string.
     * @param len The length of the target string.
     * @return @return The String after appending spaces.
     */
    public static String appendSpace(String s, int len) {
        return appendCharacter(s, ' ', len);
    }

    /** Appends specified characters to the given String to the specified length. Nothing will be done if the length of the given String is equal to or greater than the specified length.
     * @param s The source string.
     * @param c The character(s) to be appended.
     * @param len The length of the target string.
     * @return @return The String after appending the specified character(s).
     */
    public static String appendCharacter(String s, char c, int len) {
        if (s != null) {
            StringBuffer sb = new StringBuffer().append(s);
            while (sb.length() < len) {
                sb.append(c);
            }
            return sb.toString();
        } else {
            return null;
        }
    }

    /** Checks if a given string is null or empty after trimmed.
     * @param s The String to be checked.
     * @return <PRE>true</PRE> if the given string is null or empty after trimmed, <PRE>false</PRE> otherwise.
     */
    public static boolean isEmptyString(String s) {
        return (s == null || (s.trim()).equals(EMPTY_STR));
    }

    public static boolean isNotEmptyString(String s) {
        return !isEmptyString(s);
    }

    /** Returns a String representation of the given object, empty String if it is null.
     * @param obj The Object for getting its String representation.
     * @return A String represenation of the given Object.
     */
    public static String toString(Object obj) {
        if (obj == null)
            return EMPTY_STR;
        else
            return obj.toString();
    }

    public static boolean startsWith(String src, String str) {
        if (src == null || str == null) {
            return false;
        }
        return src.startsWith(str);
    }

    public static boolean endsWith(String src, String str) {
        if (src == null || str == null) {
            return false;
        }
        return src.endsWith(str);
    }

    public static String capitalize(String str) {
        if (str == null) {
            return EMPTY_STR;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String uncapitalize(String str) {
        if (str == null) {
            return EMPTY_STR;
        }
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    /** Trims a given String. An empty String will be returned if the given String is null.
     * @param s The String to be Trimmed.
     * @return The String trimmed.
     */
    public static String trim(String s) {
        if (s == null)
            return EMPTY_STR;
        else
            return s.trim();
    }

    /** Trims a given String and then verifies its size against the specified size. If the sizes do not match, null will be returned.
     * @param s The String to be trimmed and verified.
     * @param size The size for the verification.
     * @return The trimmed String or null if the size verification failed.
     */
    public static String trimAndVerifySize(String s, int size) {

        s = trim(s);

        if (s.length() != size)
            return null;
        else
            return s;
    }

    /** Replaces all the occurences of a search string in a given String with a specified substitution.
     * @param text The String to be searched.
     * @param src The search String.
     * @param tar The replacement String.
     * @return The result String after replacing.
     */
    public static String replace(String text, String src, String tar) {
        StringBuffer sb = new StringBuffer();

        if (text == null || src == null || tar == null) {
            return text;
        } else {
            int size = text.length();
            int gap = src.length();

            for (int start = 0; start >= 0 && start < size;) {
                int i = text.indexOf(src, start);
                if (i == -1) {
                    sb.append(text.substring(start));
                    start = -1;
                } else {
                    sb.append(text.substring(start, i)).append(tar);
                    start = i + gap;
                }
            }
            return sb.toString();
        }
    }

    public static String[] splitByWholeSeparator(String str, String separator) {
        if (str == null) {
            return null;
        }

        int len = str.length();

        if (len == 0) {
            return (String[]) Collections.EMPTY_LIST.toArray();
        }

        if ((separator == null) || (EMPTY_STR.equals(separator))) {
            // Split on whitespace.
            return str.split(" ");
        }
        return null;
    }

    /** Test driver.
     * @param args The arguments.
     * TODO: Move to test pakage
     */
    public static void main(String[] args) {

        System.out.println(StringUtils.leftPad("1", 2, '0'));
        System.out.println(StringUtils.addLeadingZero("55", 1));
        System.out.println(StringUtils.addLeadingZero("56", 2));
        System.out.println(StringUtils.addLeadingZero("5", 3));
        System.out.println(StringUtils.addLeadingZero("0", 0));
        System.out.println(StringUtils.addLeadingZero("", -1));

        System.out.println(StringUtils.removeLeadingZero("00055"));
        System.out.println(StringUtils.removeLeadingZero("56"));
        System.out.println(StringUtils.removeLeadingZero("5"));
        System.out.println(StringUtils.removeLeadingZero("0"));
        System.out.println(StringUtils.removeLeadingZero(""));
    }

}