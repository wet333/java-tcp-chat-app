package online.awet.tui.renderers;

import online.awet.tui.TUILayout;
import online.awet.tui.Terminal;
import online.awet.tui.states.InputFieldState;

public class InputFieldRenderer {

    private static final String PROMPT = "> ";

    private final Terminal terminal;

    public InputFieldRenderer(Terminal terminal) {
        this.terminal = terminal;
    }

    public void render(InputFieldState state, TUILayout.Region region) {
        // Get the available content width; bail out if too narrow
        int innerWidth = region.contentWidth;
        if (innerWidth < 1) return;

        // Extract the current text and cursor position from the input state
        String text = state.getText();
        int cursorPos = state.getCursorPos();
        int promptLen = PROMPT.length();
        int visibleWidth = innerWidth - promptLen;

        if (visibleWidth < 1) return;

        // Calculate the visible text window, scrolling horizontally if the cursor exceeds the width
        int viewStart = 0;
        if (cursorPos > visibleWidth) {
            viewStart = cursorPos - visibleWidth;
        }
        int viewEnd = Math.min(text.length(), viewStart + visibleWidth);
        String visible = text.substring(viewStart, viewEnd);

        // Draw the prompt "> " followed by the visible text, padded with spaces to fill the region
        terminal.moveCursor(region.firstRow, region.contentCol);
        String content = PROMPT + visible;
        int pad = innerWidth - promptLen - visible.length();
        if (pad < 0) pad = 0;
        terminal.print(content + " ".repeat(pad));

        // Position the terminal cursor at the correct column and make it visible
        int cursorCol = region.contentCol + promptLen + (cursorPos - viewStart);
        terminal.moveCursor(region.firstRow, cursorCol);
        terminal.showCursor();

        // Flush all buffered content to the terminal
        terminal.flush();
    }
}
