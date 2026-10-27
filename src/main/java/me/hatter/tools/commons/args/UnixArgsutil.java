package me.hatter.tools.commons.args;

import java.util.*;

public class UnixArgsutil {
    public static final UnixArgs ARGS = new UnixArgs();

    public static void parseGlobalArgs(String[] args) {
        processArgs(ARGS, args);
    }

    public static void processArgs(UnixArgs unixArgs, String[] args) {
        if (args != null) {
            String lastk = null;

            for (String arg : args) {
                if (lastk != null) {
                    List<String> vs = unixArgs.keyValues.computeIfAbsent(lastk, k -> new ArrayList<>());

                    vs.add(arg);
                    lastk = null;
                } else {
                    if (arg.startsWith("---")) {
                        String ar = arg.substring(3);

                        for (char c : ar.toCharArray())
                            unixArgs.flags.add(String.valueOf(c));
                    } else if (arg.startsWith("--")) {
                        String ar = arg.substring(2);
                        int indexOfEq = ar.indexOf('=');

                        if (indexOfEq > 0) {
                            String k = ar.substring(0, indexOfEq);
                            String v = ar.substring(indexOfEq + 1);
                            List<String> vs = unixArgs.keyValues.computeIfAbsent(k, k1 -> new ArrayList<>());

                            vs.add(v);
                        } else
                            unixArgs.flags.add(ar);
                    } else if (arg.startsWith("-"))
                        lastk = arg.substring(1);
                    else
                        unixArgs.args.add(arg);
                }
            }
        }
    }
}
