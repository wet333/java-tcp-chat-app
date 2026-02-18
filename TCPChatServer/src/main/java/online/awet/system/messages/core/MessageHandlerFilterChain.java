package online.awet.system.messages.core;

import online.awet.system.Configurations;
import online.awet.system.broadcast.BroadcastManager;
import online.awet.system.core.parser.Translator;
import online.awet.system.core.parser.TranslatorException;
import online.awet.system.messages.exceptions.MessageHandlerException;
import online.awet.system.sessions.holder.SessionHolder;

public class MessageHandlerFilterChain {

    private static final MessageHandlerFilterChain instance = new MessageHandlerFilterChain();
    private final MessageHandlerRegistry registry;
    private final Translator translator;
    private final BroadcastManager broadcastManager;

    private MessageHandlerFilterChain() {
        System.out.println("Initializing MessageHandlerFilterChain...");
        registry = MessageHandlerRegistry.getInstance();
        translator = Translator.getInstance();
        broadcastManager = BroadcastManager.getInstance();
    }

    public static MessageHandlerFilterChain getInstance() {
        return instance;
    }

    public void process(SessionHolder sessionHolder, String message) {

        try {
            message = translator.translate(message);
        } catch (TranslatorException te) {
            broadcastManager.serverDirectMessage(te.getMessage(), sessionHolder.getCurrentSession());
            return;
        }

        try {
            for (MessageHandler handler : registry.getMessageHandlerMap().values()) {
                if (handler.accepts(message)) {
                    handler.process(sessionHolder, message);
                    if (!Configurations.ALLOW_MULTIPLE_MESSAGE_HANDLERS) {
                        return;
                    }
                }
            }
        } catch (MessageHandlerException mhe) {
            broadcastManager.serverDirectMessage(mhe.getMessage(), sessionHolder.getCurrentSession());
        }
    }
}