package online.awet.system.messages.handlers;

import online.awet.system.broadcast.BroadcastManager;
import online.awet.system.core.parser.ClientMessageParser;
import online.awet.system.messages.BaseMessageHandler;
import online.awet.system.messages.RegisterMessageHandler;
import online.awet.system.sessions.Session;
import online.awet.system.userManagement.FileBasedAccountManager;

import java.util.Map;

@RegisterMessageHandler
public class UserRegisterHandler extends BaseMessageHandler {

    @Override
    public boolean accepts(String message) {
        return message.startsWith("REGISTER:");
    }

    @Override
    public void handleMessage(Session session, String message) {
        BroadcastManager broadcastManager = BroadcastManager.getInstance();
        FileBasedAccountManager accountManager = FileBasedAccountManager.getInstance();
        Map<String, String> data = ClientMessageParser.parse(message);

        System.out.println(data);

        try {
            String username = data.get("username");
            String password = data.get("password");

            if (username == null || password == null) {
                throw new Exception("Missing username or password.");
            }
            accountManager.addAccount(data.get("username"), data.get("password"));

            broadcastManager.serverDirectMessage(
                    "User " + data.get("username") + " has been registered.",
                    session
            );
        } catch (Exception e) {
            broadcastManager.serverDirectMessage(
                    "Couldn't register user " + data.get("username") + ". " + e.getMessage(),
                    session
            );
        }
    }
}
