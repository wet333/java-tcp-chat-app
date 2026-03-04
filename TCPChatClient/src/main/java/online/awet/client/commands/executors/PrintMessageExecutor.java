package online.awet.client.commands.executors;

import java.util.Set;

import online.awet.commons.Command;
import online.awet.commons.CommandExecutor;
import online.awet.commons.CommandSignature;
import online.awet.commons.CommandType;
import online.awet.system.ClientContext;
import online.awet.tui.terminal.ANSIColor;
import online.awet.tui.terminal.TerminalFormatUtils;

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

        msg = TerminalFormatUtils.color(msg, ANSIColor.GRAY);

        ctx.getChatPanelState().addMessage(msg);
        ctx.notifyStateChanged();
    }
}
