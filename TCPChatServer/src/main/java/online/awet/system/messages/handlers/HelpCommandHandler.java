package online.awet.system.messages.handlers;

import online.awet.system.broadcast.BroadcastManager;
import online.awet.system.messages.core.BaseMessageHandler;
import online.awet.system.messages.core.MessageHandler;
import online.awet.system.messages.core.MessageHandlerRegistry;
import online.awet.system.messages.core.RegisterMessageHandler;
import online.awet.system.messages.handlers.extensions.HelpProvider;
import online.awet.system.sessions.Session;

import java.util.Map;

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
     * List the descriptions from all registered MessageHandlers that implements
     * the HelpProvider interface.
     *
     * @param session The session associated with the client sending the message.
     * @param message The client message to be processed.
     */
    @Override
    public void handleMessage(Session session, String message) {
        BroadcastManager broadcastManager = BroadcastManager.getInstance();
        StringBuilder helpMessage = new StringBuilder();

        Map<Class<? extends MessageHandler>, MessageHandler> hanlderList = MessageHandlerRegistry.getInstance().getMessageHandlerMap();

        helpMessage.append("\n\tCommands: \n");

        hanlderList.values().forEach(handler -> {
            if (handler instanceof HelpProvider) {
                HelpProvider helpProvider = (HelpProvider) handler;
                helpMessage.append("\t\t" + helpProvider.getDescription() + "\n");
            }
        });

        helpMessage.append("\n");

        broadcastManager.serverDirectMessage(helpMessage.toString(), session);
    }
}