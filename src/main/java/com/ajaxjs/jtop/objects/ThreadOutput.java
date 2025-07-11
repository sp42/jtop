package com.ajaxjs.jtop.objects;

import lombok.Data;

@Data
public class ThreadOutput {
    private Thread.State state;

    private long cpuTime;

    private long userTime;
}
