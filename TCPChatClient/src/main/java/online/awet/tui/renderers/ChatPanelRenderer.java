package online.awet.tui.renderers;

import online.awet.tui.TUILayout;
import online.awet.tui.Terminal;
import online.awet.tui.states.ChatPanelState;

import java.util.ArrayList;
import java.util.List;

public class ChatPanelRenderer {

    private final Terminal terminal;

    public ChatPanelRenderer(Terminal terminal) {
        this.terminal = terminal;
    }

    public void render(ChatPanelState state, TUILayout.Region region) {
        // Determine how many lines and how wide the content area is
        int availableLines = region.height;
        int innerWidth = region.contentWidth;

        if (innerWidth < 1) return;

        // Wrap all messages to fit within the content width
        List<String> wrappedLines = wrapMessages(state.getMessages(), innerWidth);

        // Calculate the maximum scroll offset based on the available lines and the number of wrapped lines
        int maxOffset = Math.max(0, wrappedLines.size() - availableLines);
        state.clampScrollOffset(maxOffset);
        int effectiveOffset = state.getScrollOffset();
        int start = Math.max(0, maxOffset - effectiveOffset);
        int end = Math.min(start + availableLines, wrappedLines.size());
        List<String> visibleLines = wrappedLines.subList(start, end);
        state.setScrollDisplayInfo(end, wrappedLines.size());

        // Draw each row: print the message line or an empty line, padded to fill the width
        for (int i = 0; i < availableLines; i++) {
            int row = region.firstRow + i;
            terminal.moveCursor(row, region.contentCol);

            String line;
            if (i < visibleLines.size()) {
                line = visibleLines.get(i);
            } else {
                line = "";
            }

            int pad = innerWidth - line.length();
            if (pad < 0) pad = 0;
            terminal.print(line + " ".repeat(pad));
        }

        // Flush all buffered content to the terminal
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
