package online.awet.system.messages.handlers;

import online.awet.system.broadcast.BroadcastManager;
import online.awet.system.core.parser.ClientMessageParser;
import online.awet.system.messages.core.BaseMessageHandler;
import online.awet.system.messages.core.RegisterMessageHandler;
import online.awet.system.messages.exceptions.MessageHandlerException;
import online.awet.system.messages.handlers.extensions.HelpProvider;
import online.awet.system.sessions.holder.SessionHolder;
import online.awet.system.userManagement.FileStorageAccountManagerImpl;

import java.util.Map;

@RegisterMessageHandler
public class UserRegisterHandler extends BaseMessageHandler implements HelpProvider {

    @Override
    public boolean accepts(String message) {
        return message.startsWith("REGISTER:");
    }

    @Override
    public void handleMessage(SessionHolder sessionHolder, String message) {
        BroadcastManager broadcastManager = BroadcastManager.getInstance();
        FileStorageAccountManagerImpl accountManager = FileStorageAccountManagerImpl.getInstance();
        Map<String, String> data = ClientMessageParser.parse(message);

        try {
            String username = data.get("username");
            String password = data.get("password");

            if (username == null || password == null) {
                throw new Exception("Missing username or password.");
            }
            accountManager.addAccount(data.get("username"), data.get("password"));

            broadcastManager.serverDirectMessage(
                    "User " + data.get("username") + " has been registered.",
                    sessionHolder.getCurrentSession()
            );
        } catch (Exception e) {
            throw new MessageHandlerException(e.getMessage(), this);
        }
    }

    @Override
    public String getHelp() {
        return "/register -username <username> -password <password>";
    }

    @Override
    public String getDescription() {
        return "/register: Allows you to register.";
    }
}
