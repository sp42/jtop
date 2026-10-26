package me.hatter.tools.commons;

import java.util.HashMap;
import java.util.Map;

public class CommonUtil {
    private static final Map<String, Boolean> classPresentMap = new HashMap<>();

    public static boolean isClassPresent(String className) {
        synchronized (classPresentMap) {
            if (classPresentMap.get(className) != null) {
                return classPresentMap.get(className);
            }
        }

        boolean found = true;

        try {
            Class.forName(className);
        } catch (ClassNotFoundException e) {
            found = false;
        }

        synchronized (classPresentMap) {
            classPresentMap.put(className, found);
        }
        return found;
    }
}
