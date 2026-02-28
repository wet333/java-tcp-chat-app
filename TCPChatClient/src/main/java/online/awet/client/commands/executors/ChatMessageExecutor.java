package online.awet.client.commands.executors;

import online.awet.commons.Command;
import online.awet.commons.CommandExecutor;
import online.awet.commons.CommandSignature;
import online.awet.commons.CommandType;
import online.awet.system.ClientContext;

import java.util.Set;

/**
 * Handles {@code CHAT_MSG} commands from the server (broadcast chat messages).
 *
 * <p>The server sends {@code {sender, msg}} to all connections. This executor
 * formats the line with a {@code > } prefix when the sender matches the local
 * user's username (own messages), so the client can visually distinguish them
 * without the server needing to pre-format per-recipient.
 */
public class ChatMessageExecutor implements CommandExecutor {

    @Override
    public CommandSignature getSignature() {
        return new CommandSignature(CommandType.CHAT_MSG, Set.of("sender", "msg"));
    }

    @Override
    public void execute(Command command) {
        String sender = command.getParams().get("sender");
        String msg = command.getParams().get("msg");

        ClientContext ctx = ClientContext.getInstance();
        String self = ctx.getStatusBarState().getUsername();

        String line = sender.equals(self)
                ? "> " + sender + ": " + msg
                :       sender + ": " + msg;

        ctx.getChatPanelState().addMessage(line);
        ctx.notifyStateChanged();
    }
}
