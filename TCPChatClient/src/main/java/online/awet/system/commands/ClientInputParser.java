package online.awet.system.commands;

import online.awet.commons.Command;
import online.awet.commons.CommandTarget;
import online.awet.commons.CommandType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Parses raw user input into {@link Command} objects targeting the server.
 *
 * <ul>
 *   <li>Plain text (no leading {@code /}) → {@code Command(SEND, SERVER, {msg: text})}
 *   <li>{@code /commandName -key val ...} → typed {@code Command} based on {@link CommandType}
 *   <li>{@code /send -username <u> -msg <m>} is automatically promoted to {@code SEND_DM}
 * </ul>
 */
public class ClientInputParser {

    public Command parse(String rawInput) {
        if (rawInput == null || rawInput.isBlank()) {
            throw new ClientInputParserException("Empty input");
        }

        rawInput = rawInput.trim();

        if (!rawInput.startsWith("/")) {
            return Command.of(CommandType.SEND, CommandTarget.SERVER, Map.of("msg", rawInput));
        }

        List<String> tokens = tokenize(rawInput);
        String commandToken = tokens.removeFirst();

        if (commandToken.equals("/")) {
            throw new ClientInputParserException("Empty command after '/'");
        }

        String commandName = commandToken.substring(1).toUpperCase();
        CommandType type;
        try {
            type = CommandType.valueOf(commandName);
        } catch (IllegalArgumentException e) {
            throw new ClientInputParserException("Unknown command: /" + commandName.toLowerCase());
        }

        Map<String, String> params = new LinkedHashMap<>();
        while (!tokens.isEmpty()) {
            String key = tokens.removeFirst();
            if (!key.startsWith("-")) {
                throw new ClientInputParserException("Expected -key, got: " + key);
            }
            key = key.substring(1);
            if (tokens.isEmpty()) {
                throw new ClientInputParserException("Missing value for parameter: -" + key);
            }
            params.put(key, tokens.removeFirst());
        }

        // /send -username <u> -msg <m> → SEND_DM for backward compatibility with old syntax
        if (type == CommandType.SEND && params.containsKey("username")) {
            type = CommandType.SEND_DM;
        }

        return Command.of(type, CommandTarget.SERVER, params);
    }

    private List<String> tokenize(String input) {
        List<String> tokens = new ArrayList<>();
        int i = 0;
        while (i < input.length()) {
            char ch = input.charAt(i);
            if (ch == ' ') {
                i++;
                continue;
            }
            if (ch == '\'' || ch == '"') {
                int close = input.indexOf(ch, i + 1);
                if (close == -1) {
                    throw new ClientInputParserException("Unclosed quote starting at position " + i);
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
