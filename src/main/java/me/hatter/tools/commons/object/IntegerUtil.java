package me.hatter.tools.commons.object;

public class IntegerUtil {


    public static Integer tryParse(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    public static int tryParse(String str, int defaultVal) {
        Integer val = tryParse(str);
        return (val == null) ? defaultVal : val;
    }
}
