package online.awet.system.commands.handlers;

import online.awet.system.core.broadcast.BroadcastManager;
import online.awet.system.core.broadcast.ClientConnection;
import online.awet.system.commands.*;
import online.awet.system.core.sessions.Session;

import java.util.Set;

@RegisterCommandHandler
public class SendHandler implements CommandHandler {

    @Override
    public Set<CommandSignature> getSignatures() {
        return Set.of(CommandSignature.of("SEND", "msg"));
    }

    @Override
    public void handle(ClientConnection connection, Command command) {
        BroadcastManager broadcastManager = BroadcastManager.getInstance();
        Session session = connection.getSession();
        String message = session.getDisplayName() + ": " + command.params().get("msg");
        broadcastManager.broadcast(message, connection);
    }
}
