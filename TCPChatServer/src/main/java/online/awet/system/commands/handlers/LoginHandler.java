package online.awet.system.commands.handlers;

import online.awet.system.broadcast.BroadcastManager;
import online.awet.system.commands.*;
import online.awet.system.commands.handlers.extensions.HelpProvider;
import online.awet.system.sessions.AuthenticatedSession;
import online.awet.system.sessions.holder.SessionHolder;
import online.awet.system.userManagement.AccountManager;
import online.awet.system.userManagement.AccountManagerException;
import online.awet.system.userManagement.User;

import java.util.Map;
import java.util.Set;

@RegisterCommandHandler
public class LoginHandler implements CommandHandler, HelpProvider {

    @Override
    public Set<CommandSignature> getSignatures() {
        return Set.of(CommandSignature.of("LOGIN", "username", "password"));
    }

    @Override
    public void handle(SessionHolder sessionHolder, Command command) {
        BroadcastManager broadcastManager = BroadcastManager.getInstance();
        AccountManager accountManager = AccountManager.getInstance();

        String username = command.params().get("username");
        String password = command.params().get("password");

        try {
            User user = accountManager.getAccount(username, password);

            sessionHolder.authenticate(Map.of(
                AuthenticatedSession.USERNAME, user.getUsername(),
                AuthenticatedSession.ALIAS, user.getUsername()
            ));

            broadcastManager.serverDirectMessage(
                "You are logged in as: " + username, sessionHolder.getCurrentSession()
            );
        } catch (AccountManagerException e) {
            broadcastManager.serverDirectMessage(e.getMessage(), sessionHolder.getCurrentSession());
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
