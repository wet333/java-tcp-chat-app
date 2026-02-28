package online.awet.client.commands.executors;

import online.awet.commons.Command;
import online.awet.commons.CommandExecutor;
import online.awet.commons.CommandSignature;
import online.awet.commons.CommandType;
import online.awet.system.ClientContext;

import java.util.Set;

/**
 * Handles {@code DM_MSG} commands from the server (direct messages).
 *
 * <p>The server sends the same {@code {sender, recipient, msg}} structure to both
 * the sender and the recipient. This executor determines direction by comparing
 * {@code sender} to the local username: own messages render as outgoing, others
 * as incoming.
 */
public class DmMessageExecutor implements CommandExecutor {

    @Override
    public CommandSignature getSignature() {
        return new CommandSignature(CommandType.DM_MSG, Set.of("sender", "recipient", "msg"));
    }

    @Override
    public void execute(Command command) {
        String sender = command.getParams().get("sender");
        String recipient = command.getParams().get("recipient");
        String msg = command.getParams().get("msg");

        ClientContext ctx = ClientContext.getInstance();
        String self = ctx.getStatusBarState().getUsername();

        String line = sender.equals(self)
                ? "> [DM to " + recipient + "]: " + msg
                : "[DM from " + sender + "]: " + msg;

        ctx.getChatPanelState().addMessage(line);
        ctx.notifyStateChanged();
    }
}
