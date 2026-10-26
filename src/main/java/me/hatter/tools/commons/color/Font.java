package me.hatter.tools.commons.color;

import me.hatter.tools.commons.screen.TermUtils;

import java.util.LinkedHashSet;
import java.util.Set;

public class Font {
    private Position position;
    private Color color;
    private boolean isBold;

    public Font(Position position, Color color, boolean isBold) {
        this.position = position;
        this.color = color;
        this.isBold = isBold;
    }

    public static Font createFont(Color color) {
        return createFont(color, false);
    }

    public static Font createFont(Position position, Color color) {
        return createFont(position, color, false);
    }

    public static Font createFont(Color color, boolean isBold) {
        return createFont(null, color, isBold);
    }

    public static Font createFont(Position position, Color color, boolean isBold) {
        return new Font(position, color, isBold);
    }

    public String display(String text) {
        StringBuilder sb = new StringBuilder();
        boolean needEndFlag = false;

        if (position != null) {
            needEndFlag = true;
            sb.append(TermUtils.CHAR_27 + "[").append(position.getRow()).append("H");
            sb.append(TermUtils.CHAR_27 + "[").append(position.getCol()).append("C");
        }

        if ((color != null) && (color.getValues() != null) && (!color.getValues().isEmpty())) {
            needEndFlag = true;
            Set<Integer> l = new LinkedHashSet<>();

            if (isBold)
                l.add(1);

            l.addAll(color.getValues());
            sb.append(TermUtils.CHAR_27 + "[").append(join(l.toArray(), ";")).append("m");
        }

        sb.append(text);

        if (needEndFlag)
            sb.append(TermUtils.RESET);

        return sb.toString();
    }

    public static final String EMPTY = "";

    public static String join(Object[] objects, String separator) {
        if (objects == null)
            return null;

        if (objects.length == 0)
            return EMPTY;

        StringBuilder sb = new StringBuilder();
        sb.append(objects[0]);

        for (int i = 1; i < objects.length; i++)
            sb.append(separator).append(objects[i]);

        return sb.toString();
    }
}
