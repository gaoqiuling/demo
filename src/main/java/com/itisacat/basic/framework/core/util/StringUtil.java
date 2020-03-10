package com.itisacat.basic.framework.core.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.util.regex.Pattern;

public class StringUtil extends StringUtils {

    private static final Pattern HTML_TAG_PATTERN = Pattern.compile("<[^>]*>");
    private static final String CHECK_ERROR_MESSAGE = "This string is empty or null!";
    private static final String NULL_MESSAGE = "Input string is null";

    private StringUtil() {}

    /**
     * 第一个字符小写
     * 
     * @param string
     * @return
     * @see
     */
    public static String firstCharLower(String string) {
        return new StringBuilder().append(Character.toLowerCase(firstCh(string))).append(string.substring(1))
                .toString();
    }

    /**
     * 第一个字符大写
     * 
     * @param string
     * @return
     * @see
     */
    public static String firstCharUpper(String string) {
        return new StringBuilder().append(Character.toUpperCase(firstCh(string))).append(string.substring(1))
                .toString();
    }

    /**
     * 第一个字母大写转小写，或者第一个字母小写转大写
     * 
     * @param string
     * @return
     * @see
     */
    public static String firstCharUpperOrLower(String string) {
        if (Character.isLowerCase(firstCh(string))) {
            return firstCharUpper(string);
        } else {
            return firstCharLower(string);
        }
    }

    /**
     * 获取第一个字符
     * 
     * @param string
     * @return
     * @see
     */
    public static String firstChar(String string) {
        return (new StringBuilder().append(firstCh(string))).toString();
    }

    public static Character firstCh(String string) {
        Assert.isTrue(EmptyUtils.isNotEmpty(string), CHECK_ERROR_MESSAGE);
        return string.charAt(0);
    }

    /**
     * 替换
     * 
     * @param string
     * @param oldString
     * @param newString
     * @return
     * @see
     */
    public static String replaceHTML(String string, String oldString, String newString) {
        Assert.notNull(string, NULL_MESSAGE);
        if (string.isEmpty()) {
            return string;
        }
        return string.replace(oldString, newString);
    }

    /**
     * 剔除所有带html标签的字符串
     * 
     * @param string
     * @return
     * @see
     */
    public static String replaceHTML(String string) {
        Assert.notNull(string, NULL_MESSAGE);
        if (string.isEmpty()) {
            return string;
        }
        return HTML_TAG_PATTERN.matcher(string).replaceAll("");
    }

    /**
     * replace oldChar -> ''
     * 
     * @param string
     * @param replaceChars
     * @return
     * @see
     */
    public static String replaceCharToNull(String string, String... replaceChars) {
        Assert.notNull(string, NULL_MESSAGE);
        if (string.isEmpty()) {
            return string;
        }
        String result = string;
        for (String replaceChar : replaceChars) {
            result = result.replace(replaceChar, "");
        }
        return result;
    }

    public static int getLength(String string) {
        Assert.notNull(string, NULL_MESSAGE);
        return string.length();
    }
}
