package online.awet.system.messages.handlers;

import online.awet.system.broadcast.BroadcastManager;
import online.awet.system.core.parser.ClientMessageParser;
import online.awet.system.core.parser.ParserException;
import online.awet.system.messages.core.BaseMessageHandler;
import online.awet.system.messages.exceptions.MessageHandlerException;
import online.awet.system.sessions.Session;

import java.util.Map;

//@RegisterMessageHandler
public class CommandEchoHandler extends BaseMessageHandler {
    @Override
    public boolean accepts(String message) {
        if (!ClientMessageParser.isACommand(message)) {
            return false;
        }
        return !ClientMessageParser.parse(message).get("command").isBlank();
    }

    @Override
    public void handleMessage(Session session, String message) throws MessageHandlerException {
        BroadcastManager broadcastManager = BroadcastManager.getInstance();
        StringBuilder response = new StringBuilder();

        try {
            Map<String, String> clientReq = ClientMessageParser.parse(message);

            response.append(session.getSessionId()).append(": Executing command --> ")
                    .append(clientReq.remove("command"))
                    .append(" {");

            clientReq.forEach((key, value) -> {
                response.append(key).append(": ").append(value);
            });
            response.append("}");

            broadcastManager.broadcast(response.toString(), session);
        } catch (ParserException e) {
            throw new MessageHandlerException(e.getMessage(), this);
        }
    }
}
