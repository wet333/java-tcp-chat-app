package online.awet.system.messages.handlers;

import online.awet.system.broadcast.BroadcastManager;
import online.awet.system.messages.BaseMessageHandler;
import online.awet.system.messages.RegisterMessageHandler;
import online.awet.system.sessions.Session;

@RegisterMessageHandler
public class HelpCommandHandler extends BaseMessageHandler {

    /**
     * Determines if this handler should process the specified message.
     * This handler only accepts messages that start with "HELP:", which
     * corresponds to the client sending the "/help" command. The client
     * translates commands into a server-compatible format before sending,
     * ensuring the server can recognize and process the command accurately.
     *
     * @param message The client message to be evaluated.
     * @return {@code true} if the message starts with "HELP:"; otherwise, {@code false}.
     */
    @Override
    public boolean accepts(String message) {
        return message.startsWith("HELP:");
    }

    /**
     * Processes the help command by sending a help message back to the requesting client.
     *
     * @param session The session associated with the client sending the message.
     * @param message The client message to be processed.
     */
    @Override
    public void handleMessage(Session session, String message) {
        BroadcastManager broadcastManager = BroadcastManager.getInstance();

        // Sample help information to send back to the client
        String helpMessage = "Available commands:\n" +
                "/help - Display available commands\n" +
                "/whisper <user> <message> - Send a private message\n" +
                "/quit - Disconnect from the chat\n";

        broadcastManager.serverDirectMessage(helpMessage, session);
    }
}