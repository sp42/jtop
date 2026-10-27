package me.hatter.tools.commons.args;

import java.util.Collections;
import java.util.List;

public class UnixArgs {
     final KList args = new KList();
     final KSet flags = new KSet();
     final KMap keyValues = new KMap();

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
        List<String> vs = keyValues.get(key);

        return (vs == null || vs.isEmpty()) ? null : vs.get(0);
    }

    public List<String> kvalues(String key) {
        List<String> vs = keyValues.get(key);

        return vs == null ? null : Collections.unmodifiableList(vs);
    }
}
