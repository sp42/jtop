package me.hatter.tools.commons.object;

public class LongUtil {


    public static Long tryParse(String str) {
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    public static long tryParse(String str, long defaultVal) {
        Long val = tryParse(str);
        return (val == null) ? defaultVal : val;
    }
}
