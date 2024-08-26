package online.awet.system.messages.handlers;

import online.awet.system.messages.BaseMessageHandler;
import online.awet.system.messages.RegisterMessageHandler;
import online.awet.system.sessions.Session;

@RegisterMessageHandler
public class TestHandler extends BaseMessageHandler {

    @Override
    public boolean accepts(String message) {
        return message.startsWith("TEST-COMMAND:");
    }

    @Override
    public void handleMessage(Session session, String message) {
        System.out.println("MESSAGE HANDLED BY TEST-HANDLER: " + message);
    }
}
