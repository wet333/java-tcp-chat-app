package online.awet.system.messages.core;

import online.awet.system.Configurations;
import online.awet.system.sessions.Session;

/**
 * Manages a single chain of MessageHandlers for client messages. It routes messages
 * to all handlers that accept them or stops after the first if
 * {@link Configurations#ALLOW_MULTIPLE_MESSAGE_HANDLERS} is false.
 */
public class MessageHandlerFilterChain {

    private static MessageHandlerFilterChain instance;

    private final MessageHandlerRegistry registry;

    /**
     * Private constructor that sets up the handler registry.
     */
    private MessageHandlerFilterChain() {
        System.out.println("Initializing MessageHandlerFilterChain...");
        registry = MessageHandlerRegistry.getInstance();
    }

    /**
     * Retrieves of MessageHandlerFilterChain instance and creates a new instance on
     * first invocation.
     *
     * @return the MessageHandlerFilterChain singleton instance
     */
    public static MessageHandlerFilterChain getInstance() {
        if (instance == null) {
            instance = new MessageHandlerFilterChain();
        }
        return instance;
    }

    /**
     * Passes the given message through each eligible handler. Stops after the first
     * handler if {@code Configurations.ALLOW_MULTIPLE_MESSAGE_HANDLERS} is false.
     *
     * @param session the session context for processing
     * @param message the client message to process
     */
    public void process(Session session, String message) {
        for (MessageHandler handler : registry.getMessageHandlerMap().values()) {
            if (handler.accepts(message)) {
                handler.process(session, message);
                if (!Configurations.ALLOW_MULTIPLE_MESSAGE_HANDLERS) {
                    return;
                }
            }
        }
    }
}