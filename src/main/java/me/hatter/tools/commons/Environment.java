package me.hatter.tools.commons;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.nio.file.FileSystems;

public class Environment {
    public static final String DEFAULT_SEPARATER = ",";

    public static final String OS_NAME = System.getProperty("os.name");
    public static final String OS_ARCH = System.getProperty("os.arch");
    public static final String OS_VERSION = System.getProperty("os.version");
    public static final String JAVA_VENDOR = System.getProperty("java.vendor");

    public static final String JAVA_HOME = System.getProperty("java.home");
    public static final String USER_DIR = System.getProperty("user.dir");
    public static final String USER_NAME = System.getProperty("user.name");
    public static final String USER_HOME = System.getProperty("user.home");
    public static final String JDK_HOME = Environment.JAVA_HOME.replaceAll("/jre(/)?$", "");

    @Getter
    @RequiredArgsConstructor
    public enum JavaVendor {
        Apple("Apple"),

        Sun("Sun"),

        Oracle("Oracle"),

        Unknown(null);

        private final String vendor;
    }

    public static JavaVendor getVendor() {
        String jv = JAVA_VENDOR.trim();

        if (jv.startsWith(JavaVendor.Apple.getVendor()))
            return JavaVendor.Apple;

        if (jv.startsWith(JavaVendor.Sun.getVendor()))
            return JavaVendor.Sun;

        if (jv.startsWith(JavaVendor.Oracle.getVendor()))
            return JavaVendor.Oracle;

        return JavaVendor.Unknown;
    }

}
