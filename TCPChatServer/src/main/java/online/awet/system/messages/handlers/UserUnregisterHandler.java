package online.awet.system.messages.handlers;

import online.awet.system.broadcast.BroadcastManager;
import online.awet.system.core.parser.ClientMessageParser;
import online.awet.system.messages.core.BaseMessageHandler;
import online.awet.system.messages.core.RegisterMessageHandler;
import online.awet.system.messages.exceptions.MessageHandlerException;
import online.awet.system.messages.handlers.extensions.HelpProvider;
import online.awet.system.sessions.holder.SessionHolder;
import online.awet.system.userManagement.AccountManager;
import online.awet.system.userManagement.AccountManagerException;

import java.util.Map;

@RegisterMessageHandler
public class UserUnregisterHandler extends BaseMessageHandler implements HelpProvider {

    @Override
    public boolean accepts(String message) {
        return message.startsWith("UNREGISTER:");
    }

    @Override
    public void handleMessage(SessionHolder sessionHolder, String message) throws MessageHandlerException {
        BroadcastManager broadcastManager = BroadcastManager.getInstance();
        AccountManager accountManager = AccountManager.getInstance();
        Map<String, String> data = ClientMessageParser.parse(message);

        try {
            String username = data.get("username");
            String password = data.get("password");

            if (username == null || password == null) {
                throw new MessageHandlerException("Missing credentials username or password", this);
            }

            if (accountManager.getAccount(username, password) == null) {
                throw new MessageHandlerException("Invalid username or password", this);
            }

            accountManager.deleteAccount(username, password);

            sessionHolder.deleteSession();

            broadcastManager.serverDirectMessage(
                    "Account " + username + " has been unregistered and you have been logged out.",
                    sessionHolder.getCurrentSession()
            );

        } catch (AccountManagerException e) {
            throw new MessageHandlerException(e.getMessage(), this);
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