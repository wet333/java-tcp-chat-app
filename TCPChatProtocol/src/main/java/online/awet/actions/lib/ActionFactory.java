package online.awet.actions.lib;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentHashMap;

public class ActionFactory {

    // Singleton functionality
    private static ActionFactory instance;

    private ActionFactory() {}

    public static ActionFactory getInstance() {
        if (instance == null) {
            instance = new ActionFactory();
        }
        return instance;
    }

    // Action instance management functionality
    private final ConcurrentHashMap<Class<? extends AbstractAction>, AbstractAction> actionInstances = new ConcurrentHashMap<>();

    public <T extends AbstractAction> T getAction(Class<T> actionClass) {
        boolean isAlreadyInstantiated = actionInstances.containsKey(actionClass);

        if (!isAlreadyInstantiated) {
            try {
                T actionInstance = actionClass.getConstructor().newInstance();
                actionInstances.put(actionClass, actionInstance);
                return actionInstance;
            } catch (
                InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e
            ) {
                System.out.println("Error while instantiating action: " + e.getMessage());
                return null;
            }
        }
        return (T) actionInstances.get(actionClass);
    }
}
