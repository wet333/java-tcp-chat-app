package online.awet.system.commands.handlers;

import online.awet.system.core.broadcast.BroadcastManager;
import online.awet.system.core.broadcast.ClientConnection;
import online.awet.system.commands.*;
import online.awet.system.commands.handlers.extensions.HelpProvider;
import online.awet.system.userManagement.AccountManager;
import online.awet.system.userManagement.AccountManagerException;
import online.awet.system.userManagement.User;

import java.io.IOException;
import java.util.Set;

@RegisterCommandHandler
public class LoginHandler implements CommandHandler, HelpProvider {

    @Override
    public Set<CommandSignature> getSignatures() {
        return Set.of(CommandSignature.of("LOGIN", "username", "password"));
    }

    @Override
    public void handle(ClientConnection connection, Command command) {
        AccountManager accountManager = AccountManager.getInstance();

        String username = command.params().get("username");
        String password = command.params().get("password");

        try {
            User user = accountManager.getAccount(username, password);
            connection.authenticate(user.getUsername(), user.getUsername());
            connection.send("You are logged in as: " + username);
        } catch (AccountManagerException | IOException e) {
            try {
                connection.send(e.getMessage());
            } catch (IOException ignored) {}
        }
    }

    @Override
    public String getHelp() {
        return "Usage: /login -username <username> -password <password>";
    }

    @Override
    public String getDescription() {
        return "/login: Allows a registered user to login.";
    }
}
