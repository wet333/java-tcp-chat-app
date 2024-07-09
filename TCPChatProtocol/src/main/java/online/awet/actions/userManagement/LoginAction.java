package online.awet.actions.userManagement;

import online.awet.action.AbstractAction;
import online.awet.action.Action;
import online.awet.action.ActionException;

public class LoginAction extends AbstractAction {

    private static final String ACTION = "REGISTER";
    private static final int ARGS_COUNT = 2;
    private static final String ACTION_ID = "/server.register";

    private static LoginAction instance;

    private LoginAction() {}

    public static Action getInstance() {
        if (instance == null) {
            instance = new LoginAction();
        }
        return instance;
    }

    public String getAction() {
        return ACTION;
    }

    public String getActionId() {
        return ACTION_ID;
    }

    public int getNumberOfArguments() {
        return ARGS_COUNT;
    }

    @Override
    public String getClientIdentifier() {
        return "";
    }

    @Override
    public String getServerIdentifier() {
        return "";
    }

    public String parseUserMessage(String message) throws ActionException {
        String[] messageParts = message.split(":");

        String action = messageParts[0];
        String username = messageParts[1];
        String password = messageParts[2];

        if (ACTION_ID.equals(action) && !username.isBlank() && !password.isBlank()) {
            return PROTOCOL_PREFIX + ":" + this.getAction() + ":" + username + ":" + password + ";";
        } else {
            throw new ActionException("Invalid use of " + ACTION_ID);
        }
    }
}
