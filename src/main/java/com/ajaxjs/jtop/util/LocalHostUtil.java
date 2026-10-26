package com.ajaxjs.jtop.util;

import lombok.Getter;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class LocalHostUtil {
    @Getter
    private static String localIp;

    static {
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            localIp = localhost.getHostAddress();
        } catch (UnknownHostException e) {
            localIp = "unknow";
        }
    }
}
