package online.awet.actions.collection.userManagement;

import online.awet.actions.lib.AbstractAction;
import online.awet.actions.lib.exceptions.ActionException;

public class RegisterAction extends AbstractAction {

    private static final String SERVER_IDENTIFIER = "REGISTER";
    private static final String CLIENT_IDENTIFIER = "/server.register";
    private static final int ARGS_COUNT = 2;

    @Override
    public String getClientIdentifier() {
        return CLIENT_IDENTIFIER;
    }
    @Override
    public String getServerIdentifier() {
        return SERVER_IDENTIFIER;
    }
    @Override
    public int getNumberOfArguments() {
        return ARGS_COUNT;
    }
}
