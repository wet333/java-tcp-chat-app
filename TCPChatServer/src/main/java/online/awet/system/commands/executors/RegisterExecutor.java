package online.awet.system.commands.executors;

import java.util.Map;
import java.util.Set;

import online.awet.commons.Command;
import online.awet.commons.CommandExecutor;
import online.awet.commons.CommandSignature;
import online.awet.commons.CommandTarget;
import online.awet.commons.CommandType;
import online.awet.system.commands.executors.extensions.HelpProvider;
import online.awet.system.core.ClientConnection;
import online.awet.system.core.ConnectionRegistry;
import online.awet.system.userManagement.AccountManager;
import online.awet.system.userManagement.AccountManagerException;

public class RegisterExecutor implements CommandExecutor, HelpProvider {

    @Override
    public CommandSignature getSignature() {
        return new CommandSignature(CommandType.REGISTER, Set.of("username", "password"));
    }

    @Override
    public void execute(Command command) {
        if (ClientConnection.currentConnection() == null) return;

        String username = command.getParams().get("username");
        String password = command.getParams().get("password");
        ConnectionRegistry registry = ConnectionRegistry.getInstance();

        try {
            AccountManager.getInstance().addAccount(username, password);
            Command registerAnouncement = Command.of(
                CommandType.PRINT_MSG, 
                CommandTarget.CLIENT,
                Map.of("msg", "User " + username + " has been registered.")
            );
            registry.sendToCurrentConnection(registerAnouncement);

        } catch (AccountManagerException e) {
            Command registerErrorMessage = Command.of(
                CommandType.PRINT_MSG, 
                CommandTarget.CLIENT,
                Map.of("msg", e.getMessage())
            );
            registry.sendToCurrentConnection(registerErrorMessage);
        }
    }

    @Override
    public String getHelp() {
        return "Usage: /register -username <username> -password <password>";
    }

    @Override
    public String getDescription() {
        return "/register: Allows you to register a new account.";
    }
}
