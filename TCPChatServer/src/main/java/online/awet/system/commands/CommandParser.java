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

        List<String> tokens = tokenize(rawInput);

        String commandToken = tokens.removeFirst();
        if ("/".equals(commandToken)) {
            throw new CommandParserException("Empty command: \"/\"");
        }

        String commandName = commandToken.substring(1).toUpperCase();
        Map<String, String> params = new LinkedHashMap<>();

        while (!tokens.isEmpty()) {
            String key = tokens.removeFirst();

            if (!key.startsWith("-")) {
                throw new CommandParserException(
                    "Expected -key, got: " + key
                    + "\nUsage: /command -key1 value1 -key2 'multi word value'"
                );
            }

            key = key.substring(1);

            if (tokens.isEmpty()) {
                throw new CommandParserException(
                    "Missing value for key: " + key
                    + "\nUsage: /command -key1 value1 -key2 'multi word value'"
                );
            }

            params.put(key, tokens.removeFirst());
        }

        return new Command(commandName, params);
    }

    private List<String> tokenize(String input) throws CommandParserException {
        List<String> tokens = new ArrayList<>();
        int i = 0;

        while (i < input.length()) {
            if (input.charAt(i) == ' ') {
                i++;
                continue;
            }

            char ch = input.charAt(i);
            if (ch == '\'' || ch == '"') {
                int close = input.indexOf(ch, i + 1);
                if (close == -1) {
                    throw new CommandParserException("Unclosed quote: " + input.substring(i));
                }
                tokens.add(input.substring(i + 1, close));
                i = close + 1;
            } else {
                int end = input.indexOf(' ', i);
                if (end == -1) end = input.length();
                tokens.add(input.substring(i, end));
                i = end;
            }
        }

        return tokens;
    }
}
