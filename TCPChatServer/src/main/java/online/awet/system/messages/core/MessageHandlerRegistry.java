package online.awet.system.messages.core;

import online.awet.system.core.SystemUtils;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Collections;
import java.util.stream.Collectors;

public class MessageHandlerRegistry {

    private static MessageHandlerRegistry instance;

    private final Map<Class<? extends MessageHandler>, MessageHandler> messageHandlerMap = new LinkedHashMap<>();

    private MessageHandlerRegistry() {
        loadHandlers();
    }

    public static MessageHandlerRegistry getInstance() {
        if (instance == null) {
            instance = new MessageHandlerRegistry();
        }
        return instance;
    }

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

    private int getMessageHandlerPriority(Class<?> clazz) {
        if (clazz.isAnnotationPresent(RegisterMessageHandler.class)) {
            RegisterMessageHandler annotation = clazz.getAnnotation(RegisterMessageHandler.class);
            return annotation.priority();
        } else {
            throw new IllegalArgumentException("Class " + clazz.getName() + " is not annotated with @RegisterMessageHandler.");
        }
    }

    public Map<Class<? extends MessageHandler>, MessageHandler> getMessageHandlerMap() {
        return Collections.unmodifiableMap(messageHandlerMap);
    }
}