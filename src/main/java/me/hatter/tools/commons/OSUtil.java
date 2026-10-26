package me.hatter.tools.commons;

public class OSUtil {
    private static final String osName = System.getProperty("os.name");

    public enum OS {
        Windows,

        MacOS,

        Linux,

        Unix,

        Unknown;
    }

    public static OS getOS() {
        String osn = notNull(osName).trim().toUpperCase();

        if (osn.contains("MAC OS")) {
            return OS.MacOS;
        }

        if (osn.contains("WINDOWS")) {
            return OS.Windows;
        }
        if (osn.contains("LINUX")) {
            return OS.Linux;
        }
        if (osn.contains("UNIX") || osn.contains("SUNOS")) {
            return OS.Unix;
        }
        return OS.Unknown;
    }

    public static final String EMPTY = "";

    public static String notNull(final String str) {
        return (str == null) ? EMPTY : str;
    }
}
