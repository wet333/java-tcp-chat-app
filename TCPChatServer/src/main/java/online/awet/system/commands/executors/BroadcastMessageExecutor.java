package online.awet.system.commands.executors;

import online.awet.commons.Command;
import online.awet.commons.CommandExecutor;
import online.awet.commons.CommandSignature;
import online.awet.commons.CommandTarget;
import online.awet.commons.CommandType;
import online.awet.system.core.ClientConnection;
import online.awet.system.core.ConnectionRegistry;

import java.util.HashMap;
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
        Map<String, Float> color = sender.getSession().getColor();

        Map<String, String> metadata = new HashMap<>();
        metadata.put("session_color", "true");
        metadata.put("r", color.get("r").toString());
        metadata.put("g", color.get("g").toString());
        metadata.put("b", color.get("b").toString());

        Command broadcastMessage = Command.of(
            CommandType.CHAT_MSG, 
            CommandTarget.CLIENT,
            Map.of("sender", senderName, "msg", text)
        );
        broadcastMessage.setMetadata(metadata);
        ConnectionRegistry.getInstance().broadcastExcept(sender.getId(), broadcastMessage);

        Command selfMessage = Command.of(
            CommandType.CHAT_MSG, 
            CommandTarget.CLIENT,
            Map.of("sender", senderName, "msg", text)
        );
        metadata.put("echo", "true");
        selfMessage.setMetadata(metadata);
        ConnectionRegistry.getInstance().sendToCurrentConnection(selfMessage);
    }
}
