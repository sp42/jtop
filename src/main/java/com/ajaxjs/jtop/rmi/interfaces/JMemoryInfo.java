package com.ajaxjs.jtop.rmi.interfaces;

import lombok.Data;

import java.beans.ConstructorProperties;
import java.io.Serializable;

@Data
public class JMemoryInfo implements Serializable {

    private static final long serialVersionUID = 1585393014793775503L;
    private JMemoryUsage heap;
    private JMemoryUsage nonHeap;

    @ConstructorProperties({"heap", "nonHeap"})
    public JMemoryInfo(JMemoryUsage heap, JMemoryUsage nonHeap) {
        this.heap = heap;
        this.nonHeap = nonHeap;
    }


}
