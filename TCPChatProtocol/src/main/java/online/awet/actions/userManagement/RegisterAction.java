package online.awet.actions.userManagement;

import online.awet.action.AbstractAction;
import online.awet.action.ActionException;

public class RegisterAction extends AbstractAction {

    private static final String SERVER_IDENTIFIER = "REGISTER";
    private static final String CLIENT_IDENTIFIER = "/server.register";
    private static final int ARGS_COUNT = 2;

    private static RegisterAction instance;

    private RegisterAction() {}

    public static RegisterAction getInstance() {
        if (instance == null) {
            instance = new RegisterAction();
        }
        return instance;
    }

    public String getAction() {
        return SERVER_IDENTIFIER;
    }

    public String getActionId() {
        return CLIENT_IDENTIFIER;
    }

    public int getNumberOfArguments() {
        return ARGS_COUNT;
    }

    @Override
    public String getClientIdentifier() {
        return CLIENT_IDENTIFIER;
    }

    @Override
    public String getServerIdentifier() {
        return SERVER_IDENTIFIER;
    }

    public String parseUserMessage(String message) throws ActionException {
        String[] messageParts = message.split(" ");

        String action = messageParts[0];
        String username = messageParts[1];
        String password = messageParts[2];

        if (CLIENT_IDENTIFIER.equals(action) && !username.isBlank() && !password.isBlank()) {
            return PROTOCOL_PREFIX + ":" + this.getAction() + ":" + username + ":" + password + ";";
        } else {
            throw new ActionException("Invalid use of " + CLIENT_IDENTIFIER);
        }
    }
}
