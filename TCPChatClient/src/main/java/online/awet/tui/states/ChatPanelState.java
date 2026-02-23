package online.awet.tui.states;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ChatPanelState {

    private final LinkedList<String> messages;
    private final int maxMessages;

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
}
