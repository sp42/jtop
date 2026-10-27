package me.hatter.tools.commons;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

public class LogUtil {
    public enum LogType {
        TRACE,

        DEBUG,

        INFO,

        WARN,

        ERROR
    }

    private static Set<LogType> LOG_TYPE_SET = new HashSet<>();

    static {
        String logTypes = System.getProperty("log.types");
        List<LogType> logTypeList = new ArrayList<>();

        if (logTypes != null) {
            String[] logTypesArray = logTypes.split("[,;]");

            for (String lt : logTypesArray) {
                for (LogType lte : LogType.values()) {
                    if (lte.name().equalsIgnoreCase(lt.trim()))
                        logTypeList.add(lte);
                }
            }
        }

        setLogTypeSet(logTypeList.isEmpty() ? Arrays.asList(LogType.values()) : logTypeList, logTypes != null);
    }

    synchronized public static void setLogTypeSet(Collection<LogType> logTypeSet, boolean printSetInfo) {
        Set<LogType> logTypes = new HashSet<>();
        logTypes.add(LogType.ERROR);

        if (logTypeSet != null)
            logTypes.addAll(logTypeSet);

        if (printSetInfo)
            System.out.println("[INFO] Log types is set to: " + logTypes);

        LOG_TYPE_SET = logTypes;
    }

    synchronized public static void info(String message) {
        message(LogType.INFO, message);
    }

    synchronized public static void error(String message) {
        message(LogType.ERROR, message);
    }

    synchronized public static void message(LogType type, String message) {
        if (type != null) {
            if (!LOG_TYPE_SET.contains(type))
                return;
        }

        StringBuilder msg = new StringBuilder((message == null) ? 16 : (message.length() + 10));

        if (type != null) {
            msg.append("[");
            msg.append(type.name());
            msg.append("] ");
        }

        msg.append(message);
        System.out.println(msg);
    }

    public static String printStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        pw.flush();

        return sw.toString();
    }
}
