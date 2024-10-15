package online.awet.system.messages;

import online.awet.system.Configurations;
import online.awet.system.core.SystemUtils;
import online.awet.system.sessions.Session;

import java.util.*;
import java.util.stream.Collectors;

/**
 * MessageHandlerFilterChain manages a chain of {@code MessageHandler} to process client messages.
 * This singleton class dynamically loads and registers handlers annotated with
 * {@code @RegisterMessageHandler} from a specified package, allowing flexible and
 * extendable message processing.
 *
 * @see Session
 * @see MessageHandler
 * @see SystemUtils#instantiateClassesAnnotatedBy(String, String)
 */
public class MessageHandlerFilterChain {

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
     * Map for storing registered message handlers. The key is the handler's class type,
     * and the value is the handler instance.
     */
    private final Map<Class<? extends MessageHandler>, MessageHandler> messageHandlerMap = new LinkedHashMap<>();

    /**
     * Initializes the filter chain, loading handlers annotated with {@code RegisterMessageHandler}
     * and sorting them by priority.
     */
    private MessageHandlerFilterChain(){
        System.out.println("Initializing MessageHandlerFilterChain...");

        // Load and register message handlers annotated with RegisterMessageHandler.
        Map<Class<? extends MessageHandler>, MessageHandler> tempMap = new HashMap<>(SystemUtils.instantiateClassesAnnotatedBy(
                "RegisterMessageHandler",
                "online.awet.system.messages.handlers"
        ));

        // Sort the map by priority and store in a LinkedHashMap to preserve order
        Map<Class<? extends MessageHandler>, MessageHandler> sortedByPriority = tempMap.entrySet()
                .stream()
                .sorted((entry1, entry2) -> Integer.compare(
                        getMessageHandlerPriority(entry2.getKey()),
                        getMessageHandlerPriority(entry1.getKey())
                ))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
        messageHandlerMap.putAll(sortedByPriority);

        // Log loaded handlers to the console for confirmation.
        System.out.println("Loaded handlers: { ");
        sortedByPriority.forEach((aClass, handler) -> {
            System.out.println("    " + aClass.getName() + ", Priority: " + getMessageHandlerPriority(aClass));
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

    /**
     * Retrieves the priority level of a message handler class based on its
     * {@code RegisterMessageHandler} annotation. Used for sorting handlers in
     * the chain by priority.
     *
     * @param clazz the class of the message handler
     * @return the priority specified in {@code RegisterMessageHandler}
     * @throws IllegalArgumentException if the class is not annotated with {@code RegisterMessageHandler}
     */
    private int getMessageHandlerPriority(Class<?> clazz) {
        if (clazz.isAnnotationPresent(RegisterMessageHandler.class)) {
            RegisterMessageHandler annotation = clazz.getAnnotation(RegisterMessageHandler.class);
            return annotation.priority();
        } else {
            throw new IllegalArgumentException("Class " + clazz.getName() + " is not annotated with @RegisterMessageHandler.");
        }
    }
}
