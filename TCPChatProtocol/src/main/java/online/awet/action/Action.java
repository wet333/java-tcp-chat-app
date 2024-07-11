package online.awet.action;

public interface Action {

    String getClientIdentifier();
    String getServerIdentifier();
    void printActionClass();

    boolean isTriggeredByClientMessage(String clientMessage);
    boolean isTriggeredByServerMessage(String serverMessage);
    String translateToServerMessage(String clientMessage);
}
