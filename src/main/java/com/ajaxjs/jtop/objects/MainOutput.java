package com.ajaxjs.jtop.objects;


import com.ajaxjs.jtop.rmi.interfaces.JClassLoadingInfo;
import com.ajaxjs.jtop.rmi.interfaces.JMemoryInfo;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class MainOutput {
    private int round;

    private int totalThreadCount;

    private long totalCpuTime;

    private long totalUserTime;

    private JMemoryInfo jMemoryInfo;

    private JClassLoadingInfo jClassLoadingInfo;

    private Map<Long, ThreadOutput> threadMap = new HashMap<>();

    public MainOutput(int round) {
        this.round = round;
    }
}
