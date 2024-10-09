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

    /**
     * Determines if this handler should process the provided message.
     *
     * <p>
     * Each implementing class defines specific criteria for message acceptance,
     * enabling the handler to filter messages based on their type or content.
     * For example, a handler might only accept messages that start with a specific
     * command prefix or follow a predefined format.
     * </p>
     *
     * <p>
     * The {@code accepts} method plays a critical role in handler selection, within
     * a {@link MessageHandlerFilterChain}, where it allows the chain to stop processing
     * once an appropriate handler is found.
     * </p>
     *
     * @param message The message to evaluate for processing.
     * @return {@code true} if the handler should process the message; otherwise, {@code false}.
     */
    boolean accepts(String message);
}
