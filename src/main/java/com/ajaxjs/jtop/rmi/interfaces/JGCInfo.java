package com.ajaxjs.jtop.rmi.interfaces;

import lombok.Data;

import java.beans.ConstructorProperties;
import java.io.Serializable;

@Data
public class JGCInfo implements Serializable {
    private static final long serialVersionUID = -2275633151997422389L;
    private String name;
    private boolean isValid;
    private String[] memoryPoolNames;
    private long collectionCount;
    private long collectionTime;

    public JGCInfo() {
    }

    @ConstructorProperties({"name", "isValid", "memoryPoolNames", "collectionCount", "collectionTime"})
    public JGCInfo(String name, boolean isValid, String[] memoryPoolNames, long collectionCount, long collectionTime) {
        this.name = name;
        this.isValid = isValid;
        this.memoryPoolNames = memoryPoolNames;
        this.collectionCount = collectionCount;
        this.collectionTime = collectionTime;
    }

}
