package online.awet.system.commands.handlers;

import online.awet.system.broadcast.BroadcastManager;
import online.awet.system.commands.*;
import online.awet.system.commands.handlers.extensions.HelpProvider;
import online.awet.system.sessions.holder.SessionHolder;
import online.awet.system.userManagement.AccountManager;
import online.awet.system.userManagement.AccountManagerException;

import java.util.Set;

@RegisterCommandHandler
public class RegisterHandler implements CommandHandler, HelpProvider {

    @Override
    public Set<CommandSignature> getSignatures() {
        return Set.of(CommandSignature.of("REGISTER", "username", "password"));
    }

    @Override
    public void handle(SessionHolder sessionHolder, Command command) {
        BroadcastManager broadcastManager = BroadcastManager.getInstance();
        AccountManager accountManager = AccountManager.getInstance();

        String username = command.params().get("username");
        String password = command.params().get("password");

        try {
            accountManager.addAccount(username, password);
            broadcastManager.serverDirectMessage(
                "User " + username + " has been registered.",
                sessionHolder.getCurrentSession()
            );
        } catch (AccountManagerException e) {
            broadcastManager.serverDirectMessage(e.getMessage(), sessionHolder.getCurrentSession());
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
