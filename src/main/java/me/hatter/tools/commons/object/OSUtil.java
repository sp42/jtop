package me.hatter.tools.commons.object;

import java.util.Arrays;

public class OSUtil {

    private static final String osName = System.getProperty("os.name");

    public static enum OS {
        Windows,

        MacOS,

        Linux,

        Unix,

        Unknown;
    }

    public static OS getOS() {
        String osn = StringUtil.notNull(osName).trim().toUpperCase();
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

    public static boolean isWindows() {
        return (getOS() == OS.Windows);
    }

    public static boolean isMacOS() {
        return (getOS() == OS.MacOS);
    }

    public static boolean isLinux() {
        return (getOS() == OS.Linux);
    }

    public static boolean isUnix() {
        return (getOS() == OS.Unix);
    }

    public static boolean isUnknown() {
        return (getOS() == OS.Unknown);
    }

    public static boolean isUnixCompatible() {
        return Arrays.asList(OS.Linux, OS.Unix, OS.MacOS).contains(getOS());
    }
}
