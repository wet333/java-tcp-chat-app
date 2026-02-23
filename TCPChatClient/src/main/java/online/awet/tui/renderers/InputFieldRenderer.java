package online.awet.tui.renderers;

import online.awet.tui.Terminal;
import online.awet.tui.states.InputFieldState;

public class InputFieldRenderer {

    private static final String PROMPT = "> ";

    private final Terminal terminal;

    public InputFieldRenderer(Terminal terminal) {
        this.terminal = terminal;
    }

    public void render(InputFieldState state, int row, int cols) {
        int innerWidth = cols - 4;
        if (innerWidth < 1) return;

        String text = state.getText();
        int cursorPos = state.getCursorPos();
        int promptLen = PROMPT.length();
        int visibleWidth = innerWidth - promptLen;

        if (visibleWidth < 1) return;

        int viewStart = 0;
        if (cursorPos > visibleWidth) {
            viewStart = cursorPos - visibleWidth;
        }
        int viewEnd = Math.min(text.length(), viewStart + visibleWidth);
        String visible = text.substring(viewStart, viewEnd);

        terminal.moveCursor(row, 2);
        String content = " " + PROMPT + visible;
        int pad = innerWidth - promptLen - visible.length();
        if (pad < 0) pad = 0;
        terminal.print(content + " ".repeat(pad) + " ");

        int cursorCol = 2 + 1 + promptLen + (cursorPos - viewStart);
        terminal.moveCursor(row, cursorCol);
        terminal.showCursor();
        terminal.flush();
    }
}
