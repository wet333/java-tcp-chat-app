package online.awet.system.commands.handlers;

import online.awet.system.broadcast.BroadcastManager;
import online.awet.system.commands.*;
import online.awet.system.commands.handlers.extensions.HelpProvider;
import online.awet.system.sessions.holder.SessionHolder;
import online.awet.system.userManagement.AccountManager;
import online.awet.system.userManagement.AccountManagerException;

import java.util.Set;

@RegisterCommandHandler
public class UnregisterHandler implements CommandHandler, HelpProvider {

    @Override
    public Set<CommandSignature> getSignatures() {
        return Set.of(CommandSignature.of("UNREGISTER", "username", "password"));
    }

    @Override
    public void handle(SessionHolder sessionHolder, Command command) {
        BroadcastManager broadcastManager = BroadcastManager.getInstance();
        AccountManager accountManager = AccountManager.getInstance();

        String username = command.params().get("username");
        String password = command.params().get("password");

        try {
            if (accountManager.getAccount(username, password) == null) {
                broadcastManager.serverDirectMessage(
                    "Invalid username or password", sessionHolder.getCurrentSession()
                );
                return;
            }

            accountManager.deleteAccount(username, password);
            sessionHolder.deleteSession();

            broadcastManager.serverDirectMessage(
                "Account " + username + " has been unregistered and you have been logged out.",
                sessionHolder.getCurrentSession()
            );
        } catch (AccountManagerException e) {
            broadcastManager.serverDirectMessage(e.getMessage(), sessionHolder.getCurrentSession());
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
