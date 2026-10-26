package me.hatter.tools.commons.color;

public class Text {

    private Font font;
    private String text;

    public Text(Font font, String text) {
        this.font = font;
        this.text = (text == null) ? "" : text;
    }

    public static Text createText(Font font, String text) {
        return new Text(font, text);
    }

    public Text createText(String text) {
        return new Text(this.font, text);
    }

    public String getText() {
        return text;
    }

    public String toString() {
        if (font == null) {
            return text;
        }
        return font.display(text);
    }
}
