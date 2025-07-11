package com.ajaxjs.jtop.management;

import com.ajaxjs.jtop.rmi.RmiServer;
import com.ajaxjs.jtop.rmi.interfaces.JClassLoadingInfo;
import com.ajaxjs.jtop.rmi.interfaces.JGCInfo;
import com.ajaxjs.jtop.rmi.interfaces.JMemoryInfo;
import com.ajaxjs.jtop.rmi.interfaces.JThreadInfo;

import javax.management.ObjectName;
import javax.management.StandardMBean;
import java.lang.management.ManagementFactory;

public class JTopImpl extends StandardMBean implements JTopMXBean {
    private static final RmiServer rmiserver = new RmiServer();
    private static final JTopMXBean jTopMXBean = new JTopImpl();

    public JTopImpl() {
        super(JTopMXBean.class, true);
    }

    synchronized public static void registerMXBean() {
        try {
            ManagementFactory.getPlatformMBeanServer().registerMBean(jTopMXBean, new ObjectName(JTOP_MXBEAN_NAME));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JMemoryInfo getMemoryInfo() {
        return rmiserver.getMemoryInfo();
    }

    @Override
    public JGCInfo[] getGCInfos() {
        return rmiserver.getGCInfos();
    }

    @Override
    public JClassLoadingInfo getClassLoadingInfo() {
        return rmiserver.getClassLoadingInfo();
    }

    @Override
    public JThreadInfo[] listThreadInfos() {
        return rmiserver.listThreadInfos();
    }
}
