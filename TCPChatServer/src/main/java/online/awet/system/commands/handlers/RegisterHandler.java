package online.awet.system.commands.handlers;

import online.awet.system.core.broadcast.ClientConnection;
import online.awet.system.commands.*;
import online.awet.system.commands.handlers.extensions.HelpProvider;
import online.awet.system.userManagement.AccountManager;
import online.awet.system.userManagement.AccountManagerException;

import java.io.IOException;
import java.util.Set;

@RegisterCommandHandler
public class RegisterHandler implements CommandHandler, HelpProvider {

    @Override
    public Set<CommandSignature> getSignatures() {
        return Set.of(CommandSignature.of("REGISTER", "username", "password"));
    }

    @Override
    public void handle(ClientConnection connection, Command command) {
        AccountManager accountManager = AccountManager.getInstance();

        String username = command.params().get("username");
        String password = command.params().get("password");

        try {
            accountManager.addAccount(username, password);
            connection.send("User " + username + " has been registered.");
        } catch (AccountManagerException | IOException e) {
            try {
                connection.send(e.getMessage());
            } catch (IOException ignored) {}
        }
    }

    @Override
    public String getHelp() {
        return "Usage: /register -username <username> -password <password>";
    }

    @Override
    public String getDescription() {
        return "/register: Allows you to register.";
    }
}
