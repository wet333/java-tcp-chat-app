package online.awet.system.commands.handlers;

import online.awet.system.core.broadcast.ClientConnection;
import online.awet.system.commands.*;
import online.awet.system.commands.handlers.extensions.HelpProvider;
import online.awet.system.userManagement.AccountManager;
import online.awet.system.userManagement.AccountManagerException;

import java.io.IOException;
import java.util.Set;

@RegisterCommandHandler
public class UnregisterHandler implements CommandHandler, HelpProvider {

    @Override
    public Set<CommandSignature> getSignatures() {
        return Set.of(CommandSignature.of("UNREGISTER", "username", "password"));
    }

    @Override
    public void handle(ClientConnection connection, Command command) {
        AccountManager accountManager = AccountManager.getInstance();

        String username = command.params().get("username");
        String password = command.params().get("password");

        try {
            if (accountManager.getAccount(username, password) == null) {
                connection.send("Invalid username or password");
                return;
            }

            accountManager.deleteAccount(username, password);
            connection.logout();
            connection.send("Account " + username + " has been unregistered and you have been logged out.");
        } catch (AccountManagerException | IOException e) {
            try {
                connection.send(e.getMessage());
            } catch (IOException ignored) {}
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
