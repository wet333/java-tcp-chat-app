package online.awet.system;

public class Configurations {

    public static final int PORT = 7560;

    public static final int THREAD_POOL_COUNT = 12;

    public static final boolean ALLOW_MULTIPLE_MESSAGE_HANDLERS = false;

    private Configurations() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
