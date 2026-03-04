package online.awet.tui.terminal;

import java.util.ArrayList;
import java.util.List;

public class TerminalString {

    private static final String ESC = "\033[";

    private final String text;
    private final List<String> codes = new ArrayList<>();

    public TerminalString(String text) {
        this.text = text;
    }

    public TerminalString bold()          { codes.add("1"); return this; }
    public TerminalString dim()           { codes.add("2"); return this; }
    public TerminalString italic()        { codes.add("3"); return this; }
    public TerminalString underline()     { codes.add("4"); return this; }
    public TerminalString reverse()       { codes.add("7"); return this; }
    public TerminalString strikethrough() { codes.add("9"); return this; }

    public TerminalString color(ANSIColor color) {
        codes.add(color.code);
        return this;
    }

    /** 24-bit truecolor foreground. r, g, b are normalized 0.0–1.0. */
    public TerminalString color(float r, float g, float b) {
        int ri = Math.round(r * 255);
        int gi = Math.round(g * 255);
        int bi = Math.round(b * 255);
        codes.add("38;2;" + ri + ";" + gi + ";" + bi);
        return this;
    }

    /** Named background color. Background code = foreground code + 10. */
    public TerminalString bgColor(ANSIColor color) {
        int bgCode = Integer.parseInt(color.code) + 10;
        codes.add(String.valueOf(bgCode));
        return this;
    }

    /** 24-bit truecolor background. r, g, b are normalized 0.0–1.0. */
    public TerminalString bgColor(float r, float g, float b) {
        int ri = Math.round(r * 255);
        int gi = Math.round(g * 255);
        int bi = Math.round(b * 255);
        codes.add("48;2;" + ri + ";" + gi + ";" + bi);
        return this;
    }

    /** Returns the text wrapped in a single combined ANSI escape sequence. */
    public String build() {
        if (codes.isEmpty()) return text;
        return ESC + String.join(";", codes) + "m" + text + ESC + "0m";
    }
}
