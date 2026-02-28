package online.awet.system.commands;

import online.awet.commons.Command;
import online.awet.commons.CommandExecutorPool;
import online.awet.system.ClientContext;

/**
 * Singleton command processor for the client side.
 *
 * <p>On startup, reflectively loads all {@link online.awet.commons.CommandExecutor}
 * implementations from the client executor package. For each incoming {@link Command}
 * received from the server, dispatches to the matching executor via {@link CommandExecutorPool}.
 */
public class ClientCommandProcessor {

    private static final String EXECUTORS_PACKAGE = "online.awet.client.commands.executors";
    private static final ClientCommandProcessor INSTANCE = new ClientCommandProcessor();

    private ClientCommandProcessor() {
        CommandExecutorPool.getInstance().loadExecutors(EXECUTORS_PACKAGE);
    }

    public static ClientCommandProcessor getInstance() {
        return INSTANCE;
    }

    public void process(Command command) {
        try {
            CommandExecutorPool.getInstance().select(command);
        } catch (IllegalArgumentException e) {
            // Graceful fallback: show raw command type in chat panel
            ClientContext ctx = ClientContext.getInstance();
            if (ctx.getChatPanelState() != null) {
                ctx.getChatPanelState().addMessage("[Unknown command received: " + command.getType() + "]");
                ctx.notifyStateChanged();
            }
        }
    }
}
