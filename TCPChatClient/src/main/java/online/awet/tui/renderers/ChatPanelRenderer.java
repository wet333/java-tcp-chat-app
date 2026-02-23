package online.awet.tui.renderers;

import online.awet.tui.Terminal;
import online.awet.tui.states.ChatPanelState;

import java.util.ArrayList;
import java.util.List;

public class ChatPanelRenderer {

    private final Terminal terminal;

    public ChatPanelRenderer(Terminal terminal) {
        this.terminal = terminal;
    }

    public void render(ChatPanelState state, int startRow, int endRow, int cols) {
        int availableLines = endRow - startRow;
        int innerWidth = cols - 4;

        if (innerWidth < 1) return;

        List<String> wrappedLines = wrapMessages(state.getMessages(), innerWidth);

        int start = Math.max(0, wrappedLines.size() - availableLines);
        List<String> visibleLines = wrappedLines.subList(start, wrappedLines.size());

        for (int i = 0; i < availableLines; i++) {
            int row = startRow + i;
            terminal.moveCursor(row, 2);

            String line;
            if (i < visibleLines.size()) {
                line = visibleLines.get(i);
            } else {
                line = "";
            }

            int pad = innerWidth - line.length();
            if (pad < 0) pad = 0;
            terminal.print(" " + line + " ".repeat(pad) + " ");
        }

        terminal.flush();
    }

    private List<String> wrapMessages(List<String> messages, int width) {
        List<String> lines = new ArrayList<>();
        for (String msg : messages) {
            if (msg.length() <= width) {
                lines.add(msg);
            } else {
                int pos = 0;
                while (pos < msg.length()) {
                    int end = Math.min(pos + width, msg.length());
                    lines.add(msg.substring(pos, end));
                    pos = end;
                }
            }
        }
        return lines;
    }
}
