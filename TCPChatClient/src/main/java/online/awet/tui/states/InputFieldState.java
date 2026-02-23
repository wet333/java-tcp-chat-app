package online.awet.tui.states;

public class InputFieldState {

    private final StringBuilder buffer;
    private int cursorPos;

    public InputFieldState() {
        this.buffer = new StringBuilder();
        this.cursorPos = 0;
    }

    public synchronized void insertChar(char c) {
        buffer.insert(cursorPos, c);
        cursorPos++;
    }

    public synchronized void deleteChar() {
        if (cursorPos > 0) {
            buffer.deleteCharAt(cursorPos - 1);
            cursorPos--;
        }
    }

    public synchronized String submit() {
        String text = buffer.toString();
        buffer.setLength(0);
        cursorPos = 0;
        return text;
    }

    public synchronized String getText() {
        return buffer.toString();
    }

    public synchronized int getCursorPos() {
        return cursorPos;
    }

    public synchronized int length() {
        return buffer.length();
    }

    public synchronized void moveCursorLeft() {
        if (cursorPos > 0) {
            cursorPos--;
        }
    }

    public synchronized void moveCursorRight() {
        if (cursorPos < buffer.length()) {
            cursorPos++;
        }
    }
}
