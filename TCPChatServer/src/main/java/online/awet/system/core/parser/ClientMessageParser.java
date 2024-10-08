package online.awet.system.core.parser;

import java.util.HashMap;
import java.util.Map;

public class ClientMessageParser {

    public static boolean isACommand(String clientMessage) {
        return clientMessage.contains(":");
    }

    public static Map<String, String> parse(String clientMessage) throws ParserException {
        Map<String, String> result = new HashMap<>();

        if (clientMessage == null) {
            throw new ParserException("Error: message cannot be null.");
        }

        String command;
        String dataPairs;

        // TODO: no data command will cause an exception
        if (isACommand(clientMessage)) {
            String[] parts = clientMessage.split(":");
            command = parts[0];
            dataPairs = parts[1];
        } else {
            throw new ParserException("Invalid format on message: " + clientMessage);
        }
        result.put("command", command);

        // Extract the key-value pairs string
        if (dataPairs.isEmpty()) {
            return result;
        }

        // Split the key-value pairs by ";"
        String[] keyValuePairs = dataPairs.split(";");
        for (String pair : keyValuePairs) {
            if (!pair.isEmpty()) {
                String[] keyValue = pair.split("=", 2);
                if (keyValue.length == 2) {
                    result.put(keyValue[0], keyValue[1]);
                } else {
                    throw new ParserException("Invalid key-value pair: " + pair);
                }
            }
        }
        return result;
    }
}
