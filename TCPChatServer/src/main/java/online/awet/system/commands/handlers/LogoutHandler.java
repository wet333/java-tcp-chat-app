package online.awet.system.commands.handlers;

import online.awet.system.broadcast.BroadcastManager;
import online.awet.system.commands.*;
import online.awet.system.commands.handlers.extensions.HelpProvider;
import online.awet.system.sessions.AuthenticatedSession;
import online.awet.system.sessions.Session;
import online.awet.system.sessions.holder.SessionHolder;

import java.util.Set;

@RegisterCommandHandler
public class LogoutHandler implements CommandHandler, HelpProvider {

    @Override
    public Set<CommandSignature> getSignatures() {
        return Set.of(CommandSignature.of("LOGOUT"));
    }

    @Override
    public void handle(SessionHolder sessionHolder, Command command) {
        BroadcastManager broadcastManager = BroadcastManager.getInstance();
        Session currentSession = sessionHolder.getCurrentSession();

        if (!(currentSession instanceof AuthenticatedSession)) {
            broadcastManager.serverDirectMessage("You are not logged in", currentSession);
            return;
        }

        String username = currentSession.getDisplayName();
        sessionHolder.deleteSession();

        broadcastManager.serverDirectMessage(
            "User " + username + " has been logged out.",
            sessionHolder.getCurrentSession()
        );
    }

    @Override
    public String getHelp() {
        return "Usage: /logout";
    }

    @Override
    public String getDescription() {
        return "/logout: Allows you to logout from your current session.";
    }
}
