package online.awet.system.commands.executors;

import online.awet.commons.Command;
import online.awet.commons.CommandExecutor;
import online.awet.commons.CommandSignature;
import online.awet.commons.CommandTarget;
import online.awet.commons.CommandType;
import online.awet.system.core.ClientConnection;
import online.awet.system.core.ConnectionRegistry;

import java.util.Map;
import java.util.Set;

public class BroadcastMessageExecutor implements CommandExecutor {

    @Override
    public CommandSignature getSignature() {
        return new CommandSignature(CommandType.SEND, Set.of("msg"));
    }

    @Override
    public void execute(Command command) {
        ClientConnection sender = ClientConnection.currentConnection();
        if (sender == null) return;

        String senderName = sender.getSession().getDisplayName();
        String text = command.getParams().get("msg");

        Command out = Command.of(CommandType.CHAT_MSG, CommandTarget.CLIENT,
            Map.of("sender", senderName, "msg", text));
        ConnectionRegistry.getInstance().broadcast(out);
    }
}
