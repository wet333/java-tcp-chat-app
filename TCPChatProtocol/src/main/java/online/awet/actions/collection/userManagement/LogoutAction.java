package online.awet.actions.collection.userManagement;

import online.awet.actions.lib.AbstractAction;

public class LogoutAction extends AbstractAction {

    private static final String SERVER_IDENTIFIER = "LOGOUT";
    private static final String CLIENT_IDENTIFIER = "/server.logout";
    private static final int ARGS_COUNT = 0;


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