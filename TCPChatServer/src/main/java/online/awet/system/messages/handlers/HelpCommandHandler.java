package online.awet.system.messages.handlers;

import online.awet.system.broadcast.BroadcastManager;
import online.awet.system.messages.core.BaseMessageHandler;
import online.awet.system.messages.core.MessageHandler;
import online.awet.system.messages.core.MessageHandlerRegistry;
import online.awet.system.messages.core.RegisterMessageHandler;
import online.awet.system.messages.handlers.extensions.HelpProvider;
import online.awet.system.sessions.Session;
import online.awet.system.sessions.holder.SessionHolder;

import java.util.Map;

@RegisterMessageHandler
public class HelpCommandHandler extends BaseMessageHandler {

    @Override
    public boolean accepts(String message) {
        return message.startsWith("HELP:");
    }

    @Override
    public void handleMessage(SessionHolder sessionHolder, String message) {
        BroadcastManager broadcastManager = BroadcastManager.getInstance();
        StringBuilder helpMessage = new StringBuilder();

        Map<Class<? extends MessageHandler>, MessageHandler> hanlderList = MessageHandlerRegistry.getInstance().getMessageHandlerMap();

        helpMessage.append("\n\tCommands: \n");
        hanlderList.values().forEach(handler -> {
            if (handler instanceof HelpProvider helpProvider) {
                helpMessage.append("\t\t").append(helpProvider.getDescription()).append("\n");
            }
        });
        helpMessage.append("\n");

        broadcastManager.serverDirectMessage(helpMessage.toString(), sessionHolder.getCurrentSession());
    }
}