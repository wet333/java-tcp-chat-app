package online.awet.client.commands.executors;

import online.awet.commons.Command;
import online.awet.commons.CommandExecutor;
import online.awet.commons.CommandSignature;
import online.awet.commons.CommandType;
import online.awet.system.ClientContext;
import online.awet.tui.terminal.TerminalFormatUtils;
import online.awet.tui.terminal.TerminalString;
import online.awet.tui.terminal.ANSIColor;

import java.util.Set;

public class ChatMessageExecutor implements CommandExecutor {

    @Override
    public CommandSignature getSignature() {
        return new CommandSignature(CommandType.CHAT_MSG, Set.of("sender", "msg"));
    }

    @Override
    public void execute(Command command) {
        String sender = command.getParams().get("sender");
        String msg = command.getParams().get("msg");
        boolean isEcho = command.getMetadata().get("echo") != null;

        ClientContext ctx = ClientContext.getInstance();

        String line;
        if (isEcho) {
            line = new TerminalString(sender + ":").color(ANSIColor.BRIGHT_YELLOW).underline().build() + " " + msg;
        } else {
            line = TerminalFormatUtils.color(sender + ": " + msg, ANSIColor.WHITE);
        }

        ctx.getChatPanelState().addMessage(line);
        ctx.notifyStateChanged();
    }
}
