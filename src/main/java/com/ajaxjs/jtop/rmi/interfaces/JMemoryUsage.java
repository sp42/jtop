package com.ajaxjs.jtop.rmi.interfaces;

import lombok.Data;

import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.lang.management.MemoryUsage;

@Data
public class JMemoryUsage implements Serializable {
    private static final long serialVersionUID = -6607655607298958936L;
    private long init;
    private long used;
    private long committed;
    private long max;

    public JMemoryUsage(MemoryUsage memoryUsage) {
        this.init = memoryUsage.getInit();
        this.used = memoryUsage.getUsed();
        this.committed = memoryUsage.getCommitted();
        this.max = memoryUsage.getMax();
    }

    @ConstructorProperties({"init", "used", "committed", "max"})
    public JMemoryUsage(long init, long used, long committed, long max) {
        this.init = init;
        this.used = used;
        this.committed = committed;
        this.max = max;
    }

}
