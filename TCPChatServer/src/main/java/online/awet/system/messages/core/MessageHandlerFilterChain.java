package online.awet.system.messages.core;

import online.awet.system.Configurations;
import online.awet.system.broadcast.BroadcastManager;
import online.awet.system.core.parser.Translator;
import online.awet.system.core.parser.TranslatorException;
import online.awet.system.messages.exceptions.MessageHandlerException;
import online.awet.system.sessions.Session;

/**
 * Manages a single chain of MessageHandlers for client messages. It routes messages
 * to all handlers that accept them or stops after the first if
 * {@link Configurations#ALLOW_MULTIPLE_MESSAGE_HANDLERS} is false.
 */
public class MessageHandlerFilterChain {

    private static MessageHandlerFilterChain instance;
    private final MessageHandlerRegistry registry;
    private final Translator translator;
    private final BroadcastManager broadcastManager;

    /**
     * Private constructor that sets up the handler registry.
     */
    private MessageHandlerFilterChain() {
        System.out.println("Initializing MessageHandlerFilterChain...");
        registry = MessageHandlerRegistry.getInstance();
        translator = Translator.getInstance();
        broadcastManager = BroadcastManager.getInstance();
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
     * Processes an incoming message by translating it and dispatching it to appropriate message handlers.
     * If translation fails, sends the error message directly to the client.
     *
     * <p>
     *  The behavior of dispatching to multiple handlers is controlled by the {@code Configurations.ALLOW_MULTIPLE_MESSAGE_HANDLERS} setting:
     *  <ul>
     *      <li>If {@code true}, the message is passed to all handlers that accept it.</li>
     *      <li>If {@code false}, the message is passed to the first handler that accepts it, and subsequent handlers are skipped.</li>
     *  </ul>
     * </p>
     *
     * @param session the current user session associated with the message
     * @param message the incoming message to be processed
     * @throws TranslatorException if an error occurs during message translation
     */
    public void process(Session session, String message) {

        // Handle all Translator's Exceptions
        try {
            message = translator.translate(message);
        } catch (TranslatorException te) {
            broadcastManager.serverDirectMessage(te.getMessage(), session);
        }

        // Handle all MessageHandler's Exceptions
        try {
            for (MessageHandler handler : registry.getMessageHandlerMap().values()) {
                if (handler.accepts(message)) {
                    handler.process(session, message);
                    if (!Configurations.ALLOW_MULTIPLE_MESSAGE_HANDLERS) {
                        return;
                    }
                }
            }
        } catch (MessageHandlerException mhe) {
            broadcastManager.serverDirectMessage(mhe.getMessage(), session);
        }
    }
}