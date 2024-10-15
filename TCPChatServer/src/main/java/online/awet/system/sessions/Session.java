package online.awet.system.sessions;

/**
 * The Session interface defines the structure for managing a client's session within the chat system.
 * Each session provides a unique identifier, which allows tracking and managing individual client connections.
 *
 * <p>
 * Implementations of this interface manage different types of sessions with specific behaviors.
 * Current implementations include:
 * <ul>
 *     <li>{@link online.awet.system.sessions.UserSession} - A session type for authenticated or logged-in users.</li>
 * </ul>
 * </p>
 *
 * <p>
 * Implementing classes should ensure that each session instance has a unique session ID,
 * which can be used for identifying and handling specific clients within the application.
 * </p>
 */
public interface Session {

    /**
     * Retrieves the unique session ID for this session.
     *
     * @return A string representing the unique identifier for the session.
     */
    String getSessionId();
}
