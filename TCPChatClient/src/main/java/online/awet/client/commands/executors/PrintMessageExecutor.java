package online.awet.client.commands.executors;

import online.awet.commons.Command;
import online.awet.commons.CommandExecutor;
import online.awet.commons.CommandSignature;
import online.awet.commons.CommandType;
import online.awet.system.ClientContext;

import java.util.Set;

/**
 * Handles {@code PRINT_MSG} commands from the server by appending the message text
 * to the chat panel and triggering a re-render.
 */
public class PrintMessageExecutor implements CommandExecutor {

    @Override
    public CommandSignature getSignature() {
        return new CommandSignature(CommandType.PRINT_MSG, Set.of("msg"));
    }

    @Override
    public void execute(Command command) {
        String msg = command.getParams().get("msg");
        ClientContext ctx = ClientContext.getInstance();
        ctx.getChatPanelState().addMessage(msg);
        ctx.notifyStateChanged();
    }
}
