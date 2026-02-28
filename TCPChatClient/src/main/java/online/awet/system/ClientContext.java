package online.awet.system;

import online.awet.commons.Command;
import online.awet.commons.CommandSerializer;
import online.awet.tui.states.ChatPanelState;
import online.awet.tui.states.StatusBarState;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Singleton that centralizes client-side TUI state and the server connection writer.
 *
 * <p>Must be initialized (via {@link #initialize}) by {@code ChatTUI} before
 * {@link online.awet.system.commands.ClientCommandProcessor} loads executors,
 * so that executors can access state at construction time if needed.
 */
public class ClientContext {

    private static ClientContext instance;

    private StatusBarState statusBarState;
    private ChatPanelState chatPanelState;
    private BufferedWriter serverWriter;
    private Runnable onStateChanged;

    private ClientContext() {}

    public static ClientContext getInstance() {
        if (instance == null) {
            instance = new ClientContext();
        }
        return instance;
    }

    public void initialize(StatusBarState statusBarState, ChatPanelState chatPanelState, BufferedWriter serverWriter, Runnable onStateChanged) {
        this.statusBarState = statusBarState;
        this.chatPanelState = chatPanelState;
        this.serverWriter = serverWriter;
        this.onStateChanged = onStateChanged;
    }

    public StatusBarState getStatusBarState() {
        return statusBarState;
    }

    public ChatPanelState getChatPanelState() {
        return chatPanelState;
    }

    public void sendToServer(Command command) throws IOException {
        serverWriter.write(CommandSerializer.toJson(command));
        serverWriter.newLine();
        serverWriter.flush();
    }

    /**
     * Invokes the re-render callback registered by {@code ChatTUI}.
     * Called by executors after mutating state.
     */
    public void notifyStateChanged() {
        if (onStateChanged != null) {
            onStateChanged.run();
        }
    }
}
