package online.awet;

/**
    The MessageProtocol class serves as an abstract base class for defining
    different actions between a client and server. It outlines the structure
    and format of the protocol messages and provides methods to generate and
    parse these messages.

    Each message follows the format:
    PROTOCOL_PREFIX:PROTOCOL_ACTION:<data>:<data>:...<data>;

    Examples:
    AWP:REGISTER:username:password;
    AWP:LOGIN:username:password;
    AWP:ADD_FRIEND:username;
    AWP:CREATE_GROUP:user1:user2:user3:user4:user5;

    Subclasses must implement the methods to handle specific actions using the protocol.
*/
public abstract class AbstractAction {
    protected static final String PROTOCOL_PREFIX = "AWP";
    protected static AbstractAction instance;

    public abstract String generateClientRequest(String... args);
    public abstract String generateServerResponse(String clientMessage);
    public abstract String getAction();

    // Prevents instantiation
    protected AbstractAction() {}

    public static synchronized AbstractAction getInstance() {
        if (instance == null) {
            return createInstance();
        }
        return instance;
    }

    protected static AbstractAction createInstance() {
        throw new UnsupportedOperationException("Subclasses must implement this method.");
    }

    // Checks if the message contains instructions for this action
    public boolean isTriggeredBy(String message) {
        if (this.validateMessageAsAction(message)) {
            message = message.replace(PROTOCOL_PREFIX + ":", "");
            return message.startsWith(this.getAction());
        }
        return false;
    }

    // Checks if the message, is a protocol related message
    private boolean validateMessageAsAction(String message) {
        return message.startsWith(PROTOCOL_PREFIX);
    }
}
