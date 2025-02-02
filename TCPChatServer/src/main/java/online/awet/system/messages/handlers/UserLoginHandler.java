package online.awet.system.messages.handlers;

import online.awet.system.broadcast.BroadcastManager;
import online.awet.system.core.parser.ClientMessageParser;
import online.awet.system.messages.core.BaseMessageHandler;
import online.awet.system.messages.core.RegisterMessageHandler;
import online.awet.system.sessions.Session;
import online.awet.system.sessions.UserSession;
import online.awet.system.userManagement.FileBasedAccountManager;
import online.awet.system.userManagement.User;

import java.util.Map;

@RegisterMessageHandler
public class UserLoginHandler extends BaseMessageHandler {
    @Override
    public boolean accepts(String message) {
        return message.startsWith("LOGIN:");
    }

    @Override
    public void handleMessage(Session session, String message) {
        BroadcastManager broadcastManager = BroadcastManager.getInstance();
        FileBasedAccountManager accountManager = FileBasedAccountManager.getInstance();
        // TODO: Add message parsing to the baseMessageHanlder base functionality???
        Map<String, String> data = ClientMessageParser.parse(message);

        System.out.println(data);

        try {
            String username = data.get("username");
            String password = data.get("password");

            if (username == null || password == null) {
                throw new Exception("Missing credentials username or password");
            }

            User user = accountManager.getAccount(username, password);

            if (user == null) {
                throw new Exception("Invalid username or password");
            }

            ((UserSession) session).setUsername(username);
            ((UserSession) session).setPassword(password);
            ((UserSession) session).setAlias(username);

            broadcastManager.serverDirectMessage(
                    "You are logged in as: " + username,
                    session
            );
        } catch (Exception e) {
            broadcastManager.serverDirectMessage(
                    "Couldn't login user " + data.get("username") + ". " + e.getMessage(),
                    session
            );
        }
    }
}
