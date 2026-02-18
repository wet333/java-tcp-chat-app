package online.awet.system.core.parser;

import java.util.*;


public class Translator {

    private static final Translator instance = new Translator();

    private final String COMMAND_HEADER_FORMAT = "%s:";

    private final String KEY_VALUE_PAIR_FORMAT = "%s=%s;";

    private Translator() {}

    public static Translator getInstance() {
        return instance;
    }

    public String translate(String clientMessage) throws TranslatorException {
        if (clientMessage == null || clientMessage.isBlank()) {
            throw new TranslatorException("Empty message, please try again.");
        }

        if (!isServerAction(clientMessage.trim())) {
            return clientMessage;
        }

        List<String> messageParts = new ArrayList<>(List.of(clientMessage.split(" ")));
        StringBuilder result = new StringBuilder();

        String command = messageParts.removeFirst();
        if ("/".equals(command)) {
            throw new TranslatorException("Error, Empty command: \"/\"?");
        }
        if (command.startsWith("/")) {
            result.append(String.format(COMMAND_HEADER_FORMAT, camelToHyphenatedCaps(command.replace("/", ""))));
        }

        while (!messageParts.isEmpty()) {
            try {
                String key = messageParts.removeFirst();
                String value = messageParts.removeFirst();

                if (!key.startsWith("-")) {
                    throw new TranslatorException(
                            "Invalid message, on: " + clientMessage.replace(key, "<!>" + key + "<!>")
                            + "\n" + "Correct usage: /command -key1 value1 -key2 value2."
                    );
                }
                key = key.replace("-", "");

                result.append(String.format(KEY_VALUE_PAIR_FORMAT, key, value));

            } catch (NoSuchElementException e) {
                throw new TranslatorException(
                        "Missing key or value, message: " + clientMessage +
                        "\n" + "Correct usage: /command -key1 value1 -key2 value2."
                );
            }
        }
        return result.toString();
    }

    public boolean isServerAction(String clientMessage) {
        return clientMessage.startsWith("/");
    }

    public String camelToHyphenatedCaps(String input) {
        if (input == null) {
            throw new TranslatorException("Command Error: input is null.");
        }
        if (input.isBlank()) {
            throw new TranslatorException("Command Error: input is blank or empty.");
        }
        if (input.matches(".*\\d.*")) {
            throw new TranslatorException("Command Error: string contains numbers, which are not allowed.");
        }

        StringBuilder result = new StringBuilder();
        result.append(Character.toUpperCase(input.charAt(0)));

        for (int i = 1; i < input.length(); i++) {
            char current = input.charAt(i);
            char previous = input.charAt(i - 1);

            if (Character.isUpperCase(current) && Character.isLowerCase(previous)) {
                result.append('-');
            }
            if (Character.isUpperCase(current) && Character.isUpperCase(previous)) {
                result.append('-');
            }

            result.append(Character.toUpperCase(current));
        }

        return result.toString();
    }
}
