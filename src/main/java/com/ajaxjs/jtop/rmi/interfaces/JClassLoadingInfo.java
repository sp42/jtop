package com.ajaxjs.jtop.rmi.interfaces;

import lombok.Data;

import java.beans.ConstructorProperties;
import java.io.Serializable;

@Data
public class JClassLoadingInfo implements Serializable {
    private static final long serialVersionUID = 8354447788997580832L;
    private long totalLoadedClassCount;
    private int loadedClassCount;
    private long unloadedClassCount;

    @ConstructorProperties({"totalLoadedClassCount", "loadedClassCount", "unloadedClassCount"})
    public JClassLoadingInfo(long totalLoadedClassCount, int loadedClassCount, long unloadedClassCount) {
        this.totalLoadedClassCount = totalLoadedClassCount;
        this.loadedClassCount = loadedClassCount;
        this.unloadedClassCount = unloadedClassCount;
    }

    public long getTotalLoadedClassCount() {
        return totalLoadedClassCount;
    }

    public void setTotalLoadedClassCount(long totalLoadedClassCount) {
        this.totalLoadedClassCount = totalLoadedClassCount;
    }

}
