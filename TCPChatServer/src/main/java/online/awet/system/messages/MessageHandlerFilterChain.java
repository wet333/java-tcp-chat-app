package online.awet.system.messages;

import online.awet.system.Configurations;
import online.awet.system.core.SystemUtils;
import online.awet.system.sessions.Session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Singleton class MessageHandlerFilterChain is responsible for managing and processing
 * client messages through a chain of registered {@code MessageHandler}.
 * Each handler in the chain can process messages in a unique way, based on the
 * message content and session information.
 *
 * <p>
 * The MessageHandlerFilterChain dynamically loads and registers message handlers
 * annotated with {@code @RegisterMessageHandler} from a specified package.
 * This allows the application to easily extend or modify message handling
 * capabilities without changing core server code.
 * </p>
 *
 * <p>
 * Typical usage:
 * <pre>
 *     MessageHandlerFilterChain.getInstance().process(session, message);
 * </pre>
 * </p>
 *
 * @see Session
 * @see MessageHandler
 * @see SystemUtils#instantiateClassesAnnotatedBy(String, String)
 */
public class MessageHandlerFilterChain {

    /**
     * Singleton instance of MessageHandlerFilterChain.
     */
    private static MessageHandlerFilterChain instance;

    /**
     * Provides global access to the singleton instance of MessageHandlerFilterChain.
     * Initializes the instance if it does not already exist.
     *
     * @return The singleton instance of MessageHandlerFilterChain.
     */
    public static MessageHandlerFilterChain getInstance() {
        if (instance == null) {
            instance = new MessageHandlerFilterChain();
        }
        return instance;
    }

    /**
     * Map storing registered message handlers. The key is the handler's class type,
     * and the value is the handler instance.
     */
    private final Map<Class<? extends MessageHandler>, MessageHandler> messageHandlerMap = new HashMap<>();

    /**
     * Private constructor that initializes the MessageHandlerFilterChain.
     * It loads and registers all message handlers annotated with {@code RegisterMessageHandler}
     * from the specified package.
     */
    private MessageHandlerFilterChain(){
        System.out.println("Initializing MessageHandlerFilterChain...");

        // Load and register message handlers annotated with RegisterMessageHandler.
        messageHandlerMap.putAll(SystemUtils.instantiateClassesAnnotatedBy(
                "RegisterMessageHandler",
                "online.awet.system.messages.handlers"
        ));

        // Log loaded handlers to the console for confirmation.
        System.out.println("Loaded handlers: { ");
        messageHandlerMap.forEach((aClass, handler) -> {
            System.out.println("    " + aClass.getName());
        });
        System.out.println("} ");
    }

    /**
     * Passes a client message through each registered {@code MessageHandler} in
     * {@code messageHandlerMap} for processing.
     *
     * <p>If {@code Configurations.ALLOW_MULTIPLE_MESSAGE_HANDLERS} is {@code false},
     * processing stops after the first handler that accepts the message; otherwise,
     * all accepting handlers will process it.</p>
     *
     * @param session The session context for message processing.
     * @param message The client message to process.
     * @see Configurations#ALLOW_MULTIPLE_MESSAGE_HANDLERS
     */
    public void process(Session session, String message) {
        for (MessageHandler handler : messageHandlerMap.values()) {
            if (handler.accepts(message)) {
                handler.process(session, message);

                if (!Configurations.ALLOW_MULTIPLE_MESSAGE_HANDLERS) {
                    return;
                }
            }
        }
    }
}
