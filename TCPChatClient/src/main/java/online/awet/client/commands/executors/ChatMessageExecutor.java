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
        var meta = command.getMetadata();

        boolean hasSessionColor = "true".equals(meta.get("session_color"));
        float r = parseFloat(meta.get("r"), 1f);
        float g = parseFloat(meta.get("g"), 1f);
        float b = parseFloat(meta.get("b"), 1f);

        ClientContext ctx = ClientContext.getInstance();

        TerminalString line = new TerminalString(sender + ":");
        if (isEcho) line = line.underline();
        if (hasSessionColor) line = line.color(r, g, b);

        ctx.getChatPanelState().addMessage(line.build() + " " + msg);
        ctx.notifyStateChanged();
    }

    private static float parseFloat(String s, float fallback) {
        if (s == null || s.isBlank()) return fallback;
        try {
            return Float.parseFloat(s);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}
