package online.awet.userManagement;

import online.awet.AbstractAction;

public class RegisterAction extends AbstractAction {

    private static final String ACTION = "REGISTER";

    public String generateClientRequest(String... args) {
        String username = args[0];
        String password = args[1];
        return PROTOCOL_PREFIX + ":" + this.getAction() + ":" + username + ":" + password + ";";
    }

    public String generateServerResponse(String clientMessage) {
        return "The user has been registered successfully.";
    }

    public String getAction() {
        return ACTION;
    }
}
