package online.awet.server;

import online.awet.AbstractAction;

import java.util.Set;

public class ActionFilter {

    private static ActionFilter instance;

    private Set<AbstractAction> protocolSet;

    private ActionFilter() {}

    public static ActionFilter getInstance() {
        if (instance == null) {
            instance = new ActionFilter();
        }
        return instance;
    }

    public void addAction(AbstractAction action) {
        protocolSet.add(action);
    }

    public void filterMessage(String message) {
        for (AbstractAction action : protocolSet) {
            if (action.isTriggeredBy(message)) {
                action.generateServerResponse(message);
            }
        }
    }

}
