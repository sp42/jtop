package com.ajaxjs.jtop.management;


import com.ajaxjs.jtop.rmi.interfaces.JClassLoadingInfo;
import com.ajaxjs.jtop.rmi.interfaces.JGCInfo;
import com.ajaxjs.jtop.rmi.interfaces.JMemoryInfo;
import com.ajaxjs.jtop.rmi.interfaces.JThreadInfo;

public interface JTopMXBean {
    String JTOP_MXBEAN_NAME = "me.hatter.management:type=JTop";

    JMemoryInfo getMemoryInfo();

    JGCInfo[] getGCInfos();

    JClassLoadingInfo getClassLoadingInfo();

    JThreadInfo[] listThreadInfos();
}
