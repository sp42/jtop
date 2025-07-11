package com.ajaxjs.jtop.rmi;

import com.ajaxjs.jtop.Agent;
import com.ajaxjs.jtop.management.JTopMXBean;
import me.hatter.tools.commons.jmx.CustomJMXConnectTool;

public class RmiClient {
    private String pid;
    private JTopMXBean jtopMXBean;

    public RmiClient(String pid) {
        this.pid = pid;
    }

    synchronized public JTopMXBean getJTopMXBean() {
        if (jtopMXBean == null) {
            CustomJMXConnectTool tool = new CustomJMXConnectTool(pid, Agent.AGENT_INIT_KEY, Agent.class);
            jtopMXBean = tool.getCustomMXBean(JTopMXBean.class, JTopMXBean.JTOP_MXBEAN_NAME);
        }

        return jtopMXBean;
    }
}
