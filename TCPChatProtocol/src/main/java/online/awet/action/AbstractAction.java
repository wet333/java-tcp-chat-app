package online.awet.action;

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
public abstract class AbstractAction implements Action {

    protected static final String PROTOCOL_PREFIX = "AWP";

    @Override
    public boolean isTriggeredByClientMessage(String clientMessage) {
        return clientMessage.startsWith(this.getClientIdentifier());
    }

    public boolean isTriggeredByServerMessage(String message) {
        if (ActionUtils.isAnAction(message)) {
            message = message.replace(PROTOCOL_PREFIX + ":", "");
            return message.startsWith(this.getServerIdentifier());
        }
        return false;
    }

    @Override
    public String translateToServerMessage(String clientMessage) {
        return "";
    }

    @Override
    public void printActionClass() {
        System.out.println("Current action: " + this.getClass());
    }

    @Override
    public abstract String getClientIdentifier();

    @Override
    public abstract String getServerIdentifier();
}
