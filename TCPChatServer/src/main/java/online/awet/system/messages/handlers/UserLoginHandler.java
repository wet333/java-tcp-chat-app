package online.awet.system.messages.handlers;

import online.awet.system.broadcast.BroadcastManager;
import online.awet.system.core.parser.ClientMessageParser;
import online.awet.system.messages.core.BaseMessageHandler;
import online.awet.system.messages.core.RegisterMessageHandler;
import online.awet.system.messages.exceptions.MessageHandlerException;
import online.awet.system.messages.handlers.extensions.HelpProvider;
import online.awet.system.sessions.Session;
import online.awet.system.sessions.UserSession;
import online.awet.system.userManagement.AccountManagerException;
import online.awet.system.userManagement.FileStorageAccountManagerImpl;
import online.awet.system.userManagement.User;

import java.util.Map;

@RegisterMessageHandler
public class UserLoginHandler extends BaseMessageHandler implements HelpProvider {
    @Override
    public boolean accepts(String message) {
        return message.startsWith("LOGIN:");
    }

    @Override
    public void handleMessage(Session session, String message) throws MessageHandlerException {
        BroadcastManager broadcastManager = BroadcastManager.getInstance();
        FileStorageAccountManagerImpl accountManager = FileStorageAccountManagerImpl.getInstance();
        Map<String, String> data = ClientMessageParser.parse(message);

        try {
            String username = data.get("username");
            String password = data.get("password");

            if (username == null || password == null) {
                throw new MessageHandlerException("Missing credentials username or password", this);
            }

            User user = accountManager.getAccount(username, password);

            if (user == null) {
                throw new MessageHandlerException("Invalid username or password", this);
            }

            ((UserSession) session).setUsername(username);
            ((UserSession) session).setPassword(password);
            ((UserSession) session).setAlias(username);

            broadcastManager.serverDirectMessage(
                    "You are logged in as: " + username,
                    session
            );
        } catch (AccountManagerException e) {
            throw new MessageHandlerException(e.getMessage(), this);
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
