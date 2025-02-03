package online.awet.system.messages.core;

import online.awet.system.messages.exceptions.MessageHandlerException;
import online.awet.system.sessions.Session;

/**
 * BaseMessageHandler provides a foundational implementation of {@link MessageHandler}
 * to streamline the creation of specific message handlers in the chat application.
 * It contains logic for determining whether a message should be handled by this
 * particular handler and for defining custom handling logic.
 *
 * <p>
 * To create a custom message handler, developers should extend BaseMessageHandler
 * and implement the {@link #accepts(String)} method, which determines whether the
 * handler will process a specific message type, and the {@link #handleMessage(Session, String)}
 * method, which defines the actual handling behavior for accepted messages.
 * </p>
 *
 * <p>
 * Example Usage:
 * <pre>
 * public class TextMessageHandler extends BaseMessageHandler {
 *     public boolean accepts(String message) {
 *         return message.startsWith("TEXT");
 *     }
 *
 *     public void handleMessage(Session session, String message) {
 *         // Handle text message logic here
 *     }
 * }
 * </pre>
 * </p>
 *
 * @see MessageHandler
 */
public abstract class BaseMessageHandler implements MessageHandler {

    /**
     * <p>Determines if this handler should process a specific message.</p>
     * <p>Implementing classes define the criteria for accepting or rejecting messages.</p>
     *
     * @param message The message to check for acceptance.
     * @return {@code True} if the message should be handled by this handler; otherwise, {@code False}.
     */
    public abstract boolean accepts(String message);

    /**
     * Handles the processing of an accepted message. Implementers define
     * the specific handling logic in this method.
     *
     * @param session The session associated with the client sending the message.
     * @param message The message to be processed.
     */
    public abstract void handleMessage(Session session, String message) throws MessageHandlerException;

    /**
     * Processes a message by first checking if it is accepted by this handler.
     * If accepted, it logs a message indicating that this handler will handle
     * the message, then calls {@link #handleMessage(Session, String)} to execute
     * the message-specific logic.
     *
     * @param session The session associated with the client sending the message.
     * @param message The message to be processed.
     */
    public void process(Session session, String message) {
        if (this.accepts(message)) {
            // TODO: use a logger
            System.out.println(this.getClass().getName() + " --will-handle--> " + message);
            this.handleMessage(session, message);
        }
    }
}
