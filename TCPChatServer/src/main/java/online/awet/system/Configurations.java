package online.awet.system;

public class Configurations {

    /**
     * The port number that the server listens on for incoming connections.
     */
    public static final int PORT = 7560;

    /**
     * The maximum number of threads in the thread pool.
     * This limits the number of concurrent clients the server can handle.
     */
    public static final int THREAD_POOL_COUNT = 12;

    /**
     * Specifies whether a single client message can be processed by multiple
     * {@code MessageHandler} instances within the {@code MessageHandlerFilterChain}.
     *
     * <p>When set to {@code true}, each {@code MessageHandler} in the chain is given
     * the opportunity to process the client message. This allows for multiple handlers
     * to respond to or manipulate the same message as it passes through the chain.</p>
     *
     * <p>When set to {@code false} (the default value), the message will be processed
     * by only the first {@code MessageHandler} that accepts it, and subsequent handlers
     * will not receive the message. This can improve performance by reducing redundant
     * processing.</p>
     *
     * <p>Use this setting with caution, as setting it to {@code true} will generate multiple responses
     * for a single message and may negatively impact performance.</p>
     */
    public static final boolean ALLOW_MULTIPLE_MESSAGE_HANDLERS = false;

    // Prevent instantiation
    private Configurations() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
