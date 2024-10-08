package online.awet.system.messages;

import online.awet.system.core.SystemUtils;
import online.awet.system.sessions.Session;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton class MessageHandlerChain is responsible for managing and processing
 * client messages through a chain of registered {@code MessageHandler}.
 * Each handler in the chain can process messages in a unique way, based on the
 * message content and session information.
 *
 * <p>
 * The MessageHandlerChain dynamically loads and registers message handlers
 * annotated with {@code @RegisterMessageHandler} from a specified package.
 * This allows the application to easily extend or modify message handling
 * capabilities without changing core server code.
 * </p>
 *
 * <p>
 * Typical usage:
 * <pre>
 *     MessageHandlerChain.getInstance().process(session, message);
 * </pre>
 * </p>
 *
 * @see Session
 * @see MessageHandler
 * @see SystemUtils#instantiateClassesAnnotatedBy(String, String)
 */
public class MessageHandlerChain {

    /**
     * Singleton instance of MessageHandlerChain.
     */
    private static MessageHandlerChain instance;

    /**
     * Map storing registered message handlers. The key is the handler's class type,
     * and the value is the handler instance.
     */
    private final Map<Class<? extends MessageHandler>, MessageHandler> messageHandlerMap = new HashMap<>();

    /**
     * Private constructor that initializes the MessageHandlerChain.
     * It loads and registers all message handlers annotated with {@code RegisterMessageHandler}
     * from the specified package.
     */
    private MessageHandlerChain(){
        System.out.println("Initializing MessageHandlerChain...");

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
     * Provides global access to the singleton instance of MessageHandlerChain.
     * Initializes the instance if it does not already exist.
     *
     * @return The singleton instance of MessageHandlerChain.
     */
    public static MessageHandlerChain getInstance() {
        if (instance == null) {
            instance = new MessageHandlerChain();
        }
        return instance;
    }

    /**
     * Processes a message for a given session by passing it through all registered handlers.
     * Each handler in {@code messageHandlerMap} processes the message independently,
     * allowing for flexible and customizable message processing logic.
     *
     * @param session The session associated with the message, providing context for handling.
     * @param message The client message to be processed by the chain of handlers.
     */
    public void process(Session session, String message) {
        messageHandlerMap.forEach((klass, instance) -> {
            instance.process(session, message);
        });
    }
}
