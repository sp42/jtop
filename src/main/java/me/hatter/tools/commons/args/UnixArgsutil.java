package me.hatter.tools.commons.args;

import java.util.*;

public class UnixArgsutil {
    public static final UnixArgs ARGS = new UnixArgs();

    public static class UnixArgs {
        private final KList args = new KList();
        private final KSet flags = new KSet();
        private final KMap keyvalues = new KMap();

        private String[] _args = null;

        public String[] args() {
            synchronized (this) {
                if (_args == null)
                    _args = args.toArray(new String[0]);
            }

            return _args;
        }

        public KSet flags() {
            return flags;
        }

        public String kvalue(String key) {
            List<String> vs = keyvalues.get(key);
            return ((vs == null) || vs.isEmpty()) ? null : vs.get(0);
        }

        public List<String> kvalues(String key) {
            List<String> vs = keyvalues.get(key);
            return (vs == null) ? null : Collections.unmodifiableList(vs);
        }

    }

    public static class KList extends ArrayList<String> {

        private static final long serialVersionUID = -156830291036307953L;
    }

    public static class KSet extends LinkedHashSet<String> {

        private static final long serialVersionUID = 178791470286450518L;

        public KSet() {
        }

        public boolean containsAny(String key, String... keys) {
            if (this.contains(key))
                return true;

            if (keys != null) {
                for (String s : keys) {
                    if (this.contains(s))
                        return true;
                }
            }

            return false;
        }
    }

    public static class KMap extends LinkedHashMap<String, List<String>> {
        private static final long serialVersionUID = -156830291036307953L;
    }

    public static void parseGlobalArgs(String[] args) {
        processArgs(ARGS, args);
    }

    public static void processArgs(UnixArgs unixArgs, String[] args) {
        if (args != null) {
            String lastk = null;

            for (String arg : args) {
                if (lastk != null) {
                    List<String> vs = unixArgs.keyvalues.computeIfAbsent(lastk, k -> new ArrayList<>());

                    vs.add(arg);
                    lastk = null;
                } else {
                    if (arg.startsWith("---")) {
                        String ar = arg.substring(3);
                        for (char c : ar.toCharArray()) {
                            unixArgs.flags.add(String.valueOf(c));
                        }
                    } else if (arg.startsWith("--")) {
                        String ar = arg.substring(2);
                        int indexOfEq = ar.indexOf('=');

                        if (indexOfEq > 0) {
                            String k = ar.substring(0, indexOfEq);
                            String v = ar.substring(indexOfEq + 1);

                            List<String> vs = unixArgs.keyvalues.computeIfAbsent(k, k1 -> new ArrayList<>());

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
