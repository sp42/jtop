package com.ajaxjs.jtop.objects;


import com.ajaxjs.jtop.rmi.interfaces.JClassLoadingInfo;
import com.ajaxjs.jtop.rmi.interfaces.JMemoryInfo;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@RequiredArgsConstructor
public class MainOutput {
    private final int round;

    private int totalThreadCount;

    private long totalCpuTime;

    private long totalUserTime;

    private JMemoryInfo jMemoryInfo;

    private JClassLoadingInfo jClassLoadingInfo;

    private Map<Long, ThreadOutput> threadMap = new HashMap<>();
}