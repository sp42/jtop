package com.ajaxjs.jtop.util;

import java.util.HashMap;
import java.util.Map;

public class JDKUtil {
    public static enum JDKVersion {
        JDK1_0, JDK1_1, JDK1_2, JDK1_3, JDK1_4,

        JDK1_5, JDK1_6, JDK1_7, JDK1_8, Unknown
    }

    private static final Map<JDKVersion, Integer> jdkVersionMap = new HashMap<>();

    static {
        jdkVersionMap.put(JDKVersion.JDK1_0, 10);
        jdkVersionMap.put(JDKVersion.JDK1_1, 11);
        jdkVersionMap.put(JDKVersion.JDK1_2, 12);
        jdkVersionMap.put(JDKVersion.JDK1_3, 13);
        jdkVersionMap.put(JDKVersion.JDK1_4, 14);
        jdkVersionMap.put(JDKVersion.JDK1_5, 15);
        jdkVersionMap.put(JDKVersion.JDK1_6, 16);
        jdkVersionMap.put(JDKVersion.JDK1_7, 17);
        jdkVersionMap.put(JDKVersion.JDK1_8, 18);
    }

    private static final String javaSpecVersion = System.getProperty("java.specification.version");

    public static boolean isOrAbove(JDKVersion version) {
        Integer internalIndex = jdkVersionMap.get(version);
        if (internalIndex == null)
            return false;

        Integer currentInternalIndex = jdkVersionMap.get(getJDKVersion());

        if (currentInternalIndex == null)
            return false;

        return (internalIndex >= currentInternalIndex);
    }

    public static JDKVersion getJDKVersion() {
        if ("1.0".equals(javaSpecVersion)) {
            return JDKVersion.JDK1_0;
        } else if ("1.1".equals(javaSpecVersion)) {
            return JDKVersion.JDK1_1;
        } else if ("1.2".equals(javaSpecVersion)) {
            return JDKVersion.JDK1_2;
        } else if ("1.3".equals(javaSpecVersion)) {
            return JDKVersion.JDK1_3;
        } else if ("1.4".equals(javaSpecVersion)) {
            return JDKVersion.JDK1_4;
        } else if ("1.5".equals(javaSpecVersion)) {
            return JDKVersion.JDK1_5;
        } else if ("1.6".equals(javaSpecVersion)) {
            return JDKVersion.JDK1_6;
        } else if ("1.7".equals(javaSpecVersion)) {
            return JDKVersion.JDK1_7;
        } else if ("1.8".equals(javaSpecVersion)) {
            return JDKVersion.JDK1_8;
        }

        return JDKVersion.Unknown;
    }
}
