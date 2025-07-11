package com.ajaxjs.jtop.rmi;

import com.ajaxjs.jtop.rmi.interfaces.*;
import me.hatter.tools.commons.reflect.ReflectUtil;

import java.lang.management.*;
import java.util.ArrayList;
import java.util.List;

public class RmiServer {
    public void receiveMessage(String x) {
        System.out.println(x);
    }

    public RmiServer() {
    }

    public JMemoryInfo getMemoryInfo() {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        return new JMemoryInfo(new JMemoryUsage(memoryMXBean.getHeapMemoryUsage()),
                new JMemoryUsage(memoryMXBean.getNonHeapMemoryUsage()));
    }

    public JGCInfo[] getGCInfos() {
        List<JGCInfo> jgcInfos = new ArrayList<JGCInfo>();
        List<GarbageCollectorMXBean> garbageCollectorMXBeans = ManagementFactory.getGarbageCollectorMXBeans();

        for (GarbageCollectorMXBean garbageCollectorMXBean : garbageCollectorMXBeans) {
            JGCInfo jgcInfo = new JGCInfo();
            jgcInfo.setName(garbageCollectorMXBean.getName());
            jgcInfo.setValid(garbageCollectorMXBean.isValid());
            jgcInfo.setMemoryPoolNames(garbageCollectorMXBean.getMemoryPoolNames());
            jgcInfo.setCollectionCount(garbageCollectorMXBean.getCollectionCount());
            jgcInfo.setCollectionTime(garbageCollectorMXBean.getCollectionTime());
            jgcInfos.add(jgcInfo);
        }

        return jgcInfos.toArray(new JGCInfo[0]);
    }

    public JClassLoadingInfo getClassLoadingInfo() {
        ClassLoadingMXBean classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();
        return new JClassLoadingInfo(classLoadingMXBean.getTotalLoadedClassCount(),
                classLoadingMXBean.getLoadedClassCount(),
                classLoadingMXBean.getUnloadedClassCount());
    }

    @SuppressWarnings("restriction")
    public JThreadInfo[] listThreadInfos() {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        ThreadInfo[] tis = threadMXBean.dumpAllThreads(false, false);
        JThreadInfo[] jtis = new JThreadInfo[tis.length];

        for (int i = 0; i < tis.length; i++) {
            long threadId = tis[i].getThreadId();
            long cpuTime = 0L;
            long userTime = 0L;

            if (threadMXBean.isThreadCpuTimeSupported() && threadMXBean.isThreadCpuTimeEnabled()) {
                cpuTime = ManagementFactory.getThreadMXBean().getThreadCpuTime(threadId);
                userTime = ManagementFactory.getThreadMXBean().getThreadUserTime(threadId);
            }

            long alloctedBytes = 0L;
            // only in sun/oracle JDK, OpenJDK cannot find "class com.sun.management.ThreadMXBean"
            if (ReflectUtil.isClassPresent("com.sun.management.ThreadMXBean")) {
                com.sun.management.ThreadMXBean sunThreadMxBean = (com.sun.management.ThreadMXBean) ManagementFactory.getThreadMXBean();
                if (sunThreadMxBean.isThreadAllocatedMemorySupported()
                        && sunThreadMxBean.isThreadAllocatedMemoryEnabled()) {
                    alloctedBytes = sunThreadMxBean.getThreadAllocatedBytes(threadId);
                }
            }

            jtis[i] = new JThreadInfo(tis[i], cpuTime, userTime, alloctedBytes);
        }

        return jtis;
    }
}
