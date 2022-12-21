package com.bs.bsboard.util;

public class StringUtil {
    public static String trimToNull(String str) {
        if (str == null) {
            return null;
        }

        String str0 =  str.trim();
        return str0.length() == 0 ? null : str;
    }

    public static String trimToEmpty(String str) {
        if (str == null) {
            return "";
        }

        return  str.trim();
    }

    public static boolean isNotEmpty(String str) {
        return (str != null && str.length() > 0) ? true: false;
    }
}
