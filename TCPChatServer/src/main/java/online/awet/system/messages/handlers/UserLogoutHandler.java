package online.awet.system.messages.handlers;

import online.awet.system.broadcast.BroadcastManager;
import online.awet.system.messages.core.BaseMessageHandler;
import online.awet.system.messages.core.RegisterMessageHandler;
import online.awet.system.messages.exceptions.MessageHandlerException;
import online.awet.system.messages.handlers.extensions.HelpProvider;
import online.awet.system.sessions.AuthenticatedSession;
import online.awet.system.sessions.Session;
import online.awet.system.sessions.holder.SessionHolder;

@RegisterMessageHandler
public class UserLogoutHandler extends BaseMessageHandler implements HelpProvider {

    @Override
    public boolean accepts(String message) {
        return message.startsWith("LOGOUT");
    }

    @Override
    public void handleMessage(SessionHolder sessionHolder, String message) throws MessageHandlerException {
        BroadcastManager broadcastManager = BroadcastManager.getInstance();
        Session currentSession = sessionHolder.getCurrentSession();

        if (!(currentSession instanceof AuthenticatedSession)) {
            throw new MessageHandlerException("You are not logged in", this);
        }

        if (currentSession.isAuthenticated()) {
            String username = ((AuthenticatedSession) currentSession).getDisplayName();

            sessionHolder.deleteSession();

            broadcastManager.serverDirectMessage(
                    "User " + username + " has been logged out.",
                    sessionHolder.getCurrentSession()
            );
        }
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