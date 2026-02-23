package online.awet.tui;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

public class Terminal {

    private static final String ESC = "\033[";

    private final PrintStream out;
    private InputStream ttyInput;
    private String originalSttySettings;
    private int rows;
    private int cols;
    private Runnable onResize;

    public Terminal() {
        this.out = System.out;
    }

    public void enableRawMode() throws IOException {
        originalSttySettings = stty("-g").trim();
        stty("raw -echo -icanon");
        ttyInput = new FileInputStream("/dev/tty");
        updateSize();

        // Add callback for window resize terminal event
        sun.misc.Signal.handle(new sun.misc.Signal("WINCH"), signal -> {
            try { updateSize(); } catch (IOException ignored) {}
            if (onResize != null) onResize.run();
        });
    }

    public void setOnResize(Runnable onResize) {
        this.onResize = onResize;
    }

    public void disableRawMode() {
        try {
            if (originalSttySettings != null) {
                stty(originalSttySettings);
            }
        } catch (IOException ignored) {}
        try {
            if (ttyInput != null) {
                ttyInput.close();
            }
        } catch (IOException ignored) {}
        showCursor();
        out.flush();
    }

    public void updateSize() throws IOException {
        String size = stty("size").trim();
        String[] parts = size.split("\\s+");
        if (parts.length == 2) {
            rows = Integer.parseInt(parts[0]);
            cols = Integer.parseInt(parts[1]);
        } else {
            rows = 24;
            cols = 80;
        }
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public int readChar() throws IOException {
        if (ttyInput == null) {
            throw new IOException("Terminal not in raw mode");
        }
        return ttyInput.read();
    }

    public boolean hasInput() throws IOException {
        if (ttyInput == null) return false;
        return ttyInput.available() > 0;
    }

    public void moveCursor(int row, int col) {
        out.print(ESC + row + ";" + col + "H");
    }

    public void clearScreen() {
        out.print(ESC + "2J");
        out.print(ESC + "H");
    }

    public void clearLine() {
        out.print(ESC + "2K");
    }

    public void hideCursor() {
        out.print(ESC + "?25l");
    }

    public void showCursor() {
        out.print(ESC + "?25h");
    }

    public void resetColor() {
        out.print(ESC + "0m");
    }

    public void setColor(Color color) {
        out.print(ESC + color.code + "m");
    }

    public void setBold() {
        out.print(ESC + "1m");
    }

    public void print(String text) {
        out.print(text);
    }

    public void flush() {
        out.flush();
    }

    private String stty(String args) throws IOException {
        ProcessBuilder pb = new ProcessBuilder("/bin/sh", "-c", "stty " + args + " < /dev/tty");
        pb.redirectErrorStream(true);
        Process process = pb.start();
        String result = new String(process.getInputStream().readAllBytes());
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return result;
    }

    public enum Color {
        RED("31"),
        GREEN("32"),
        YELLOW("33"),
        BLUE("34"),
        MAGENTA("35"),
        CYAN("36"),
        WHITE("37"),
        GRAY("90"),
        BRIGHT_GREEN("92"),
        BRIGHT_YELLOW("93"),
        BRIGHT_CYAN("96");

        final String code;

        Color(String code) {
            this.code = code;
        }
    }
}
