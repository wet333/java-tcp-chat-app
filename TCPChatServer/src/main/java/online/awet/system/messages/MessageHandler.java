package online.awet.system.messages;

import online.awet.system.sessions.Session;

/**
 * The MessageHandler interface defines the contract for handling and processing
 * client messages within the chat application. Each implementing class processes
 * specific types of messages or commands based on the content and context provided.
 *
 * <p>
 * This interface is extended by {@link BaseMessageHandler}, an abstract class that
 * provides a base implementation for custom message handlers. Implementers of
 * BaseMessageHandler need only specify the acceptance criteria and handling behavior
 * for each message type, making it easy to add new handlers to the system.
 * </p>
 *
 * @see BaseMessageHandler
 * @see Session
 */
public interface MessageHandler {

    /**
     * Processes a client message within the context of the specified session.
     *
     * <p>The processing behavior depends on the implementing class, which determines
     * how the message should be handled based on its type or content.</p>
     *
     * <p>Access to the user's {@link Session} is required to modify the user's state
     * and to facilitate sending a response back to the client.</p>
     *
     * @param session The session associated with the client sending the message.
     * @param message The message to be processed.
     */
    void process(Session session, String message);
}
