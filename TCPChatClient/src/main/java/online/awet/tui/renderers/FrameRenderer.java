package online.awet.tui.renderers;

import online.awet.tui.Terminal;

public class FrameRenderer {

    private static final char TOP_LEFT = '┌';
    private static final char TOP_RIGHT = '┐';
    private static final char BOTTOM_LEFT = '└';
    private static final char BOTTOM_RIGHT = '┘';
    private static final char HORIZONTAL = '─';
    private static final char VERTICAL = '│';
    private static final char T_LEFT = '├';
    private static final char T_RIGHT = '┤';

    private final Terminal terminal;

    public FrameRenderer(Terminal terminal) {
        this.terminal = terminal;
    }

    public void render(int rows, int cols, int statusBarRow, int chatStartRow, int inputSeparatorRow, int inputRow) {
        terminal.hideCursor();

        String horizontalLine = String.valueOf(HORIZONTAL).repeat(cols - 2);

        terminal.moveCursor(statusBarRow, 1);
        terminal.print(TOP_LEFT + horizontalLine + TOP_RIGHT);

        terminal.moveCursor(chatStartRow, 1);
        terminal.print(T_LEFT + horizontalLine + T_RIGHT);

        terminal.moveCursor(inputSeparatorRow, 1);
        terminal.print(T_LEFT + horizontalLine + T_RIGHT);

        terminal.moveCursor(inputRow + 1, 1);
        terminal.print(BOTTOM_LEFT + horizontalLine + BOTTOM_RIGHT);

        for (int r = statusBarRow + 1; r < inputRow + 1; r++) {
            if (r == chatStartRow || r == inputSeparatorRow) continue;
            terminal.moveCursor(r, 1);
            terminal.print(String.valueOf(VERTICAL));
            terminal.moveCursor(r, cols);
            terminal.print(String.valueOf(VERTICAL));
        }

        terminal.flush();
    }
}
