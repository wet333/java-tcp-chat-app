package online.awet.system.commands;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CommandParser {

    public Command parse(String rawInput) throws CommandParserException {
        if (rawInput == null || rawInput.isBlank()) {
            throw new CommandParserException("Empty message");
        }

        rawInput = rawInput.trim();

        if (!rawInput.startsWith("/")) {
            return new Command("SEND", Map.of("msg", rawInput));
        }

        List<String> parts = new ArrayList<>(List.of(rawInput.split(" ")));

        String commandToken = parts.removeFirst();
        if ("/".equals(commandToken)) {
            throw new CommandParserException("Empty command: \"/\"");
        }

        String commandName = commandToken.substring(1).toUpperCase();
        Map<String, String> params = new LinkedHashMap<>();

        while (!parts.isEmpty()) {
            String key = parts.removeFirst();

            if (!key.startsWith("-")) {
                throw new CommandParserException(
                    "Expected -key, got: " + key
                    + "\nUsage: /command -key1 value1 -key2 value2"
                );
            }

            key = key.substring(1);

            if (parts.isEmpty()) {
                throw new CommandParserException(
                    "Missing value for key: " + key
                    + "\nUsage: /command -key1 value1 -key2 value2"
                );
            }

            params.put(key, parts.removeFirst());
        }

        return new Command(commandName, params);
    }
}
