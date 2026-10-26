package me.hatter.tools.commons.jmx;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

@SuppressWarnings("restriction")
public class RemoteManagementFactory {

    private final MBeanServerConnection connection;

    public RemoteManagementFactory(MBeanServerConnection connection) {
        this.connection = connection;
    }

    public <T> T newPlatformMXBeanProxy(String mxbeanName, Class<T> mxbeanInterface) {
        try {
            ObjectName objName = new ObjectName(mxbeanName);
            boolean emitter = connection.isInstanceOf(objName, NOTIF_EMITTER);

            return JMX.newMXBeanProxy(connection, objName, mxbeanInterface, emitter);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static final String NOTIF_EMITTER = "javax.management.NotificationEmitter";
}
