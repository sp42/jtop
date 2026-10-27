package me.hatter.tools.commons.args;

import java.util.LinkedHashSet;

public  class KSet extends LinkedHashSet<String> {
    private static final long serialVersionUID = 178791470286450518L;

    public boolean containsAny(String key, String... keys) {
        if (contains(key))
            return true;

        if (keys != null) {
            for (String s : keys) {
                if (contains(s))
                    return true;
            }
        }

        return false;
    }
}