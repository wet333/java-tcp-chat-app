package online.awet.system.commands.handlers;

import online.awet.system.broadcast.BroadcastManager;
import online.awet.system.commands.*;
import online.awet.system.sessions.Session;
import online.awet.system.sessions.holder.SessionHolder;

import java.util.Set;

@RegisterCommandHandler
public class SendHandler implements CommandHandler {

    @Override
    public Set<CommandSignature> getSignatures() {
        return Set.of(CommandSignature.of("SEND", "msg"));
    }

    @Override
    public void handle(SessionHolder sessionHolder, Command command) {
        BroadcastManager broadcastManager = BroadcastManager.getInstance();
        Session session = sessionHolder.getCurrentSession();
        String message = session.getDisplayName() + ": " + command.params().get("msg");
        broadcastManager.broadcast(message, session);
    }
}
