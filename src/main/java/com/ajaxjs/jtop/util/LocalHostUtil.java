package com.ajaxjs.jtop.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class LocalHostUtil {

    private static String localIp;

    static {
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            localIp = localhost.getHostAddress();
        } catch (UnknownHostException e) {
            localIp = "unknow";
        }
    }

    public static String getLocalIp() {
        return localIp;
    }
}
