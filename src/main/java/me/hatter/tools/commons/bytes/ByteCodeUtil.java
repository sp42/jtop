package me.hatter.tools.commons.bytes;

import me.hatter.tools.commons.object.StringUtil;

import java.util.concurrent.atomic.AtomicBoolean;

public class ByteCodeUtil {


    public static String getPrimaryTypeName(char c) {
        String className = null;
        switch (c) {
            case 'V':
                className = "void";
                break;
            case 'Z':
                className = "boolean";
                break;
            case 'C':
                className = "char";
                break;
            case 'B':
                className = "byte";
                break;
            case 'S':
                className = "short";
                break;
            case 'I':
                className = "int";
                break;
            case 'F':
                className = "float";
                break;
            case 'J':
                className = "long";
                break;
            case 'D':
                className = "double";
                break;
        }
        return className;
    }
}
