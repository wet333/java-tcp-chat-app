package online.awet.tui.states;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ChatPanelState {

    private final LinkedList<String> messages;
    private final int maxMessages;
    private int scrollOffset = 0;
    private int lastVisibleLine = 0;
    private int totalWrappedLines = 0;

    public ChatPanelState(int maxMessages) {
        this.messages = new LinkedList<>();
        this.maxMessages = maxMessages;
    }

    public ChatPanelState() {
        this(500);
    }

    public synchronized void addMessage(String message) {
        messages.addLast(message);
        while (messages.size() > maxMessages) {
            messages.removeFirst();
        }
    }

    public synchronized List<String> getMessages() {
        return Collections.unmodifiableList(new ArrayList<>(messages));
    }

    public synchronized int getMessageCount() {
        return messages.size();
    }

    public synchronized void scrollUp(int lines) {
        scrollOffset += lines;
    }

    public synchronized void scrollDown(int lines) {
        scrollOffset = Math.max(0, scrollOffset - lines);
    }

    public synchronized void resetScroll() {
        scrollOffset = 0;
    }

    public synchronized int getScrollOffset() {
        return scrollOffset;
    }

    public synchronized void clampScrollOffset(int maxAllowed) {
        scrollOffset = Math.min(scrollOffset, maxAllowed);
    }

    public synchronized void setScrollDisplayInfo(int lastVisibleLine, int totalWrappedLines) {
        this.lastVisibleLine = lastVisibleLine;
        this.totalWrappedLines = totalWrappedLines;
    }

    public synchronized int getLastVisibleLine() {
        return lastVisibleLine;
    }

    public synchronized int getTotalWrappedLines() {
        return totalWrappedLines;
    }
}
