package online.awet.system.messages.core;

import online.awet.system.core.SystemUtils;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Singleton registry that loads, sorts by priority, and stores all available MessageHandler instances.
 * Handlers are loaded from a predefined package based on the presence of the {@code RegisterMessageHandler} annotation.
 */
public class MessageHandlerRegistry {

    private static MessageHandlerRegistry instance;

    private final Map<Class<? extends MessageHandler>, MessageHandler> messageHandlerMap = new LinkedHashMap<>();

    /**
     * Private constructor to enforce singleton pattern.
     * Loads all registered message handlers.
     */
    private MessageHandlerRegistry() {
        loadHandlers();
    }

    /**
     * Returns the singleton instance of MessageHandlerRegistry.
     *
     * @return the MessageHandlerRegistry instance
     */
    public static MessageHandlerRegistry getInstance() {
        if (instance == null) {
            instance = new MessageHandlerRegistry();
        }
        return instance;
    }

    /**
     * Loads message handlers using SystemUtils, sorts them by priority (highest first),
     * and stores them in an unmodifiable map.
     */
    private void loadHandlers() {
        System.out.println("Loading MessageHandlers...");

        Map<Class<? extends MessageHandler>, MessageHandler> tempMap = new HashMap<>(SystemUtils.instantiateClassesAnnotatedBy(
                "RegisterMessageHandler",
                "online.awet.system.messages.handlers"
        ));

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

        System.out.println("Loaded handlers: { ");
        sortedByPriority.forEach((clazz, handler) -> {
            System.out.println("    " + clazz.getName() + ", Priority: " + getMessageHandlerPriority(clazz));
        });
        System.out.println("} ");
    }

    /**
     * Retrieves the priority of a MessageHandler class based on its {@code RegisterMessageHandler} annotation.
     *
     * @param clazz the MessageHandler class
     * @return the priority value from the annotation
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

    /**
     * Provides an unmodifiable map of registered MessageHandlers.
     *
     * @return map where the key is the handler's class and the value is the handler instance
     */
    public Map<Class<? extends MessageHandler>, MessageHandler> getMessageHandlerMap() {
        return Collections.unmodifiableMap(messageHandlerMap);
    }
}