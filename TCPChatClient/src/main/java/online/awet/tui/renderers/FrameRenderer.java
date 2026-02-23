package online.awet.tui.renderers;

import online.awet.tui.TUILayout;
import online.awet.tui.Terminal;

import java.util.List;

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

    public void render(TUILayout layout) {
        // Hide the cursor while drawing the frame to avoid flickering
        terminal.hideCursor();

        // Prepare a horizontal line segment that spans the full width minus the two corners
        int cols = layout.totalCols();
        String horizontalLine = String.valueOf(HORIZONTAL).repeat(cols - 2);
        List<Integer> separators = layout.separatorRows();

        // Draw the top border: ┌───────┐
        terminal.moveCursor(layout.topBorderRow(), 1);
        terminal.print(TOP_LEFT + horizontalLine + TOP_RIGHT);

        // Draw each horizontal separator row: ├───────┤
        for (int sepRow : separators) {
            terminal.moveCursor(sepRow, 1);
            terminal.print(T_LEFT + horizontalLine + T_RIGHT);
        }

        // Draw the bottom border: └───────┘
        terminal.moveCursor(layout.bottomBorderRow(), 1);
        terminal.print(BOTTOM_LEFT + horizontalLine + BOTTOM_RIGHT);

        // Draw the left and right vertical borders "│" for every row between top and bottom
        for (int r = layout.topBorderRow() + 1; r < layout.bottomBorderRow(); r++) {
            if (separators.contains(r)) continue;
            terminal.moveCursor(r, 1);
            terminal.print(String.valueOf(VERTICAL));
            terminal.moveCursor(r, cols);
            terminal.print(String.valueOf(VERTICAL));
        }

        // Flush all buffered content to the terminal
        terminal.flush();
    }
}
