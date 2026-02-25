package online.awet.system.commands.handlers;

import online.awet.system.commands.*;
import online.awet.system.commands.handlers.extensions.HelpProvider;
import online.awet.system.core.broadcast.BroadcastManager;
import online.awet.system.core.broadcast.ClientConnection;
import online.awet.system.core.sessions.Session;

import java.io.IOException;
import java.util.Set;

@RegisterCommandHandler
public class DirectMessageHandler implements CommandHandler, HelpProvider {

    @Override
    public Set<CommandSignature> getSignatures() {
        return Set.of(CommandSignature.of("SEND", "username", "msg"));
    }

    @Override
    public void handle(ClientConnection connection, Command command) {
        Session session = connection.getSession();
        String targetUsername = command.params().get("username");
        String msg = command.params().get("msg");

        try {
            if (targetUsername.equals(session.getUsername())) {
                connection.send("You cannot send a private message to yourself.");
                return;
            }

            BroadcastManager broadcastManager = BroadcastManager.getInstance();
            String formatted = "[DM from " + session.getDisplayName() + "]: " + msg;

            boolean sent = broadcastManager.sendToUser(targetUsername, formatted);
            if (sent) {
                connection.send("> [DM to " + targetUsername + "]: " + msg);
            } else {
                connection.send("User '" + targetUsername + "' is not connected.");
            }
        } catch (IOException ignored) {}
    }

    @Override
    public String getHelp() {
        return "Usage: /send -username <target> -msg '<message>'";
    }

    @Override
    public String getDescription() {
        return "/send -username -msg '<message>': Send a private message to a specific user.";
    }
}
