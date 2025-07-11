package com.ajaxjs.jtop.rmi.interfaces;

import lombok.Data;

import java.beans.ConstructorProperties;

@Data
public class StackTraceElement {
    private static final long serialVersionUID = 6992337162326171013L;

    private String className;
    private String methodName;
    private String fileName;
    private int lineNumber;

    public StackTraceElement(java.lang.StackTraceElement ste) {
        this.className = ste.getClassName();
        this.methodName = ste.getMethodName();
        this.fileName = ste.getFileName();
        this.lineNumber = ste.getLineNumber();
    }

    @ConstructorProperties({"className", "methodName", "fileName", "lineNumber"})
    public StackTraceElement(String declaringClass, String methodName, String fileName, int lineNumber) {
        if (declaringClass == null)
            throw new NullPointerException("Declaring class is null");
        if (methodName == null)
            throw new NullPointerException("Method name is null");

        this.className = declaringClass;
        this.methodName = methodName;
        this.fileName = fileName;
        this.lineNumber = lineNumber;
    }

    public boolean isNativeMethod() {
        return lineNumber == -2;
    }

    public String toString() {
        return getClassName()
                + "."
                + methodName
                + (isNativeMethod() ? "(Native Method)" : (fileName != null && lineNumber >= 0 ? "(" + fileName + ":"
                + lineNumber + ")" : (fileName != null ? "("
                + fileName
                + ")" : "(Unknown Source)")));
    }

    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof StackTraceElement)) return false;
        StackTraceElement e = (StackTraceElement) obj;

        return e.className.equals(className) && e.lineNumber == lineNumber && eq(methodName, e.methodName)
                && eq(fileName, e.fileName);
    }

    private static boolean eq(Object a, Object b) {
        return a == b || (a != null && a.equals(b));
    }

    public int hashCode() {
        int result = 31 * className.hashCode() + methodName.hashCode();
        result = 31 * result + (fileName == null ? 0 : fileName.hashCode());
        result = 31 * result + lineNumber;

        return result;
    }

    public static StackTraceElement[] convert(java.lang.StackTraceElement[] stes) {
        if (stes == null)
            return null;

        StackTraceElement[] res = new StackTraceElement[stes.length];

        for (int i = 0; i < stes.length; i++)
            res[i] = new StackTraceElement(stes[i]);

        return res;
    }
}
