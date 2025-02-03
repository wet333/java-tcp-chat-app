package online.awet.system.messages.handlers;

import online.awet.system.broadcast.BroadcastManager;
import online.awet.system.core.parser.ClientMessageParser;
import online.awet.system.messages.core.BaseMessageHandler;
import online.awet.system.messages.core.MessageHandlerFilterChain;
import online.awet.system.messages.core.RegisterMessageHandler;
import online.awet.system.sessions.Session;
import online.awet.system.sessions.UserSession;

/**
 * {@code DefaultHandler} is a catch-all message handler that processes any client message
 * that does not match specific criteria of other handlers. As it always returns {@code true}
 * from the {@link #accepts(String)} method, it serves as a fallback handler, ensuring that
 * any message reaching this point will be processed and broadcast to all connected clients.
 *
 * <p>
 * This handler is registered in the {@link MessageHandlerFilterChain}
 * through the {@link RegisterMessageHandler} annotation, enabling it to participate in the
 * message processing chain automatically. It prepends the client's session ID to the message,
 * broadcasting the modified message to all connected clients via {@link BroadcastManager}.
 * </p>
 *
 * @see BaseMessageHandler
 * @see BroadcastManager
 */
@RegisterMessageHandler(priority = 0)
public class DefaultHandler extends BaseMessageHandler {

    /**
     * Accepts any message passed to it, making this handler the default/fallback handler
     * in the message handling chain.
     *
     * @param message The client message to be evaluated.
     * @return {@code true}, indicating this handler accepts all messages.
     */
    @Override
    public boolean accepts(String message) {
        return true;
    }

    /**
     * Handles the message by broadcasting it to all connected clients. The message
     * is modified to include the client's session ID as a prefix, indicating the
     * message's origin.
     *
     * @param session The session associated with the client sending the message.
     * @param message The client message to be broadcasted.
     */
    @Override
    public void handleMessage(Session session, String message) {
        BroadcastManager broadcastManager = BroadcastManager.getInstance();

        // Any command that has no handlers will reach this point, to prevent leaking data accidentally
        // Send a private error msg to the user.
        if (ClientMessageParser.isACommand(message)) {
            message = "Invalid command: " + message.replace(":", "") + ". There are no handlers for this command.";
            broadcastManager.serverDirectMessage(message, session);
            return;
        }

        String identifier = ((UserSession) session).getAlias() != null ? ((UserSession) session).getAlias() : session.getSessionId();
        String messageMod =  identifier + ": " + message;
        broadcastManager.broadcast(messageMod, session);
    }
}
