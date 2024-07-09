package online.awet.action;

public class ActionUtils {

    public static boolean isAnAction(String message) {
        return message.startsWith(AbstractAction.PROTOCOL_PREFIX);
    }

}
