package online.awet.tui.terminal;

import java.util.regex.Pattern;

public class TerminalFormatUtils {

    private static final String ESC = "\033[";

    public static final Pattern ANSI_PATTERN = Pattern.compile("\\033\\[[0-9;]*[A-Za-z]");

    // Returns the number of visible (printable) columns in a string
    public static int visualLength(String s) {
        return ANSI_PATTERN.matcher(s).replaceAll("").length();
    }

    // Removes all ANSI escape sequences from a string
    public static String stripAnsi(String s) {
        return ANSI_PATTERN.matcher(s).replaceAll("");
    }

    public static String bold(String text)          { return wrap(text, "1"); }
    public static String dim(String text)           { return wrap(text, "2"); }
    public static String italic(String text)        { return wrap(text, "3"); }
    public static String underline(String text)     { return wrap(text, "4"); }
    public static String reverse(String text)       { return wrap(text, "7"); }
    public static String strikethrough(String text) { return wrap(text, "9"); }

    public static String color(String text, ANSIColor color) {
        return wrap(text, color.code);
    }

    public static String bgColor(String text, ANSIColor color) {
        int bgCode = Integer.parseInt(color.code) + 10;
        return wrap(text, String.valueOf(bgCode));
    }

    /** 24-bit truecolor foreground. r, g, b are normalized 0.0–1.0. */
    public static String color(String text, float r, float g, float b) {
        int ri = Math.round(r * 255);
        int gi = Math.round(g * 255);
        int bi = Math.round(b * 255);
        return wrap(text, "38;2;" + ri + ";" + gi + ";" + bi);
    }

    /** 24-bit truecolor background. r, g, b are normalized 0.0–1.0. */
    public static String bgColor(String text, float r, float g, float b) {
        int ri = Math.round(r * 255);
        int gi = Math.round(g * 255);
        int bi = Math.round(b * 255);
        return wrap(text, "48;2;" + ri + ";" + gi + ";" + bi);
    }

    private static String wrap(String text, String code) {
        return ESC + code + "m" + text + ESC + "0m";
    }
}
