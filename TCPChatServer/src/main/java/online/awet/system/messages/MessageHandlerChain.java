package online.awet.system.messages;

import online.awet.system.core.SystemUtils;
import online.awet.system.sessions.Session;

import java.util.HashMap;
import java.util.Map;

public class MessageHandlerChain {

    private static MessageHandlerChain instance;

    private final Map<Class<? extends MessageHandler>, MessageHandler> messageHandlerMap = new HashMap<>();

    private MessageHandlerChain(){
        System.out.println("Initializing MessageHandlerChain...");

        messageHandlerMap.putAll(SystemUtils.instantiateClassesAnnotatedBy(
                "RegisterMessageHandler",
                "online.awet.system.messages.handlers"
        ));

        System.out.println("Loaded handlers: { ");
        messageHandlerMap.forEach((aClass, handler) -> {
            System.out.println("    " + aClass.getName());
        });
        System.out.println("} ");
    }

    public static MessageHandlerChain getInstance() {
        if (instance == null) {
            instance = new MessageHandlerChain();
        }
        return instance;
    }

    public void process(Session session, String message) {
        messageHandlerMap.forEach((klass, instance) -> {
            instance.process(session, message);
        });
    }
}
