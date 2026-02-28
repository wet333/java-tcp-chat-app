package online.awet.system.commands.executors;

import online.awet.commons.Command;
import online.awet.commons.CommandExecutor;
import online.awet.commons.CommandSignature;
import online.awet.commons.CommandTarget;
import online.awet.commons.CommandType;
import online.awet.system.core.ClientConnection;
import online.awet.system.core.ConnectionRegistry;
import online.awet.system.userManagement.AccountManager;
import online.awet.system.userManagement.AccountManagerException;

import java.util.Map;
import java.util.Set;

public class UnregisterExecutor implements CommandExecutor, HelpProvider {

    @Override
    public CommandSignature getSignature() {
        return new CommandSignature(CommandType.UNREGISTER, Set.of("username", "password"));
    }

    @Override
    public void execute(Command command) {
        ClientConnection connection = ClientConnection.currentConnection();
        if (connection == null) return;

        String username = command.getParams().get("username");
        String password = command.getParams().get("password");
        ConnectionRegistry registry = ConnectionRegistry.getInstance();

        try {
            AccountManager.getInstance().deleteAccount(username, password);
            connection.logout();

            registry.sendToCurrentConnection(Command.of(CommandType.UPDATE_STATUS, CommandTarget.CLIENT,
                Map.of("authenticated", "false", "username", "")));

            registry.sendToCurrentConnection(Command.of(CommandType.PRINT_MSG, CommandTarget.CLIENT,
                Map.of("msg", "Account " + username + " has been unregistered and you have been logged out.")));

        } catch (AccountManagerException e) {
            registry.sendToCurrentConnection(Command.of(CommandType.PRINT_MSG, CommandTarget.CLIENT,
                Map.of("msg", e.getMessage())));
        }
    }

    @Override
    public String getHelp() {
        return "Usage: /unregister -username <username> -password <password>";
    }

    @Override
    public String getDescription() {
        return "/unregister: Allows you to delete your account and logout.";
    }
}
