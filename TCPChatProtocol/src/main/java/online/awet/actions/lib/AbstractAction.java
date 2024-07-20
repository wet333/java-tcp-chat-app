package online.awet.actions.lib;

import online.awet.actions.lib.exceptions.InvalidActionArgumentsException;

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

    public abstract String getClientIdentifier();
    public abstract String getServerIdentifier();
    public abstract int getNumberOfArguments();

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

    public String translateToServerMessage(String clientMessage) throws InvalidActionArgumentsException {
        String[] messageParts = clientMessage.split(" ");
        StringBuilder serverMessage = new StringBuilder();

        serverMessage.append(PROTOCOL_PREFIX);

        if (messageParts.length == (this.getNumberOfArguments() + 1)) {
            boolean isActionChecked = false;
            for (String arg : messageParts) {
                if (!isActionChecked && this.getClientIdentifier().equals(arg)) {
                    serverMessage.append(":").append(getServerIdentifier());
                } else {
                    serverMessage.append(":").append(arg);
                }
            }
            serverMessage.append(";");
            return serverMessage.toString();
        } else {
            throw new InvalidActionArgumentsException(this.getClass().toString());
        }
    }

    public void printActionClass() {
        System.out.println("Current action: " + this.getClass());
    }
}
