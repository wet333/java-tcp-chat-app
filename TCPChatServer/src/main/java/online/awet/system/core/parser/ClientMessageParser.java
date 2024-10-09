package online.awet.system.core.parser;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@code ClientMessageParser} class is responsible for parsing messages received from the client
 * and transforming them into a structured format that the server can process. It interprets commands
 * and their associated data, allowing the server to handle various client actions.
 *
 * <p>
 * Client messages follow a specific format: {@code COMMAND:key=data;key2=data2;}, where "COMMAND"
 * denotes the action to be performed, and "data" contains additional key-value pairs separated by
 * semicolons. For example, the message {@code HELP:topic=commands} would be parsed to identify "HELP"
 * as the command, and "topic" as a key with "commands" as its value.
 * </p>
 *
 * <p>Example usage:</p>
 * <pre>
 *     String message = "HELP:topic=commands";
 *     Map<String, String> parsedMessage = ClientMessageParser.parse(message);
 *     // parsedMessage -> { "command" : "HELP", "topic" : "commands" }
 * </pre>
 *
 * @see ParserException
 */
public class ClientMessageParser {

    /**
     * Determines if a message is a command by checking if it contains a colon separator.
     * Messages identified as commands can then be parsed further.
     *
     * @param clientMessage The message to check.
     * @return {@code true} if the message contains a colon, indicating it follows the
     *         command format; {@code false} otherwise.
     */
    public static boolean isACommand(String clientMessage) {
        return clientMessage.contains(":");
    }

    /**
     * Parses a client message, extracting the command and any associated key-value data.
     * The command is stored under the "command" key, while additional data pairs
     * are stored as individual key-value entries in the resulting map.
     *
     * <p>
     * Expected format: {@code COMMAND:key=value;key2=value2;}. The message is split
     * by the colon to separate the command from data pairs. Data pairs are then split by
     * semicolons and parsed as key-value pairs.
     * </p>
     *
     * <p>
     * Example inputs and outputs:
     * <ul>
     *     <li>Input: {@code "HELP:topic=commands;"} -> Output: {@code { "command": "HELP", "topic": "commands" }}</li>
     *     <li>Input: {@code "LOGIN:user=guest;password=1234;"} -> Output: {@code { "command": "LOGIN", "user": "guest", "password": "1234" }}</li>
     *     <li>Input: {@code "LOGOUT:"} -> Output: {@code { "command": "LOGOUT" }}</li>
     * </ul>
     * </p>
     *
     * @param clientMessage The client message to parse.
     * @return A map containing the parsed command and data key-value pairs.
     * @throws ParserException if the message format is invalid or if key-value pairs are malformed.
     */
    public static Map<String, String> parse(String clientMessage) throws ParserException {
        Map<String, String> result = new HashMap<>();

        if (clientMessage == null) {
            throw new ParserException("Error: message cannot be null.");
        }

        String command;
        String dataPairs;

        // Check if message is a command and parse components
        if (isACommand(clientMessage)) {
            String[] parts = clientMessage.split(":");
            command = parts[0];
            dataPairs = (parts.length > 1) ? parts[1] : null;
        } else {
            throw new ParserException("Invalid format on message: " + clientMessage);
        }
        result.put("command", command);

        // If no data pairs exist, return the result with only the command
        if (dataPairs == null || dataPairs.isEmpty()) {
            return result;
        }

        // Parse data pairs, which are separated by semicolons
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
