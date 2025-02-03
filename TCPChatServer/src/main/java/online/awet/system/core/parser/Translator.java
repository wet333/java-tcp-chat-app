package online.awet.system.core.parser;

import java.util.*;

// TODO: Should i make the Translator a static method that translates the clients messages in the ClientMessageParser?

/**
 * The {@code Translator} class is a singleton utility for translating client messages into a standardized
 * command format. It supports translating command headers and key-value pairs and validating
 * input.
 */
public class Translator {

    /**
     * Singleton instance of the {@code Translator}.
     */
    public static Translator instance;

    /**
     * Template for formatting command headers as "COMMAND:".
     */
    private final String COMMAND_HEADER_FORMAT = "%s:";

    /**
     * Template for formatting key-value pairs as "key=value;".
     */
    private final String KEY_VALUE_PAIR_FORMAT = "%s=%s;";

    /**
     * Private constructor to enforce a singleton pattern.
     */
    private Translator() {}

    /**
     * Returns the singleton instance of the {@code Translator}.
     *
     * @return the {@code Translator} instance.
     */
    public static Translator getInstance() {
        if (instance == null) {
            instance = new Translator();
        }
        return instance;
    }

    /**
     * Translates a client message into a formatted command string with key-value pairs.
     *
     * @param clientMessage the raw client message to translate.
     * @return a formatted string containing the command and key-value pairs.
     * @throws TranslatorException if the input is null, blank, or improperly formatted.
     *
     * <p>Example:</p>
     * <pre>
     * {@code
     * Translator translator = Translator.getInstance();
     * String input = "/command -key1 value1 -key2 value2";
     * String output = translator.translate(input);
     * // Output: "COMMAND:KEY1=value1;KEY2=value2;"
     * }
     * </pre>
     */
    public String translate(String clientMessage) {
        if (clientMessage == null || clientMessage.isBlank()) {
            throw new TranslatorException("Empty message, please try again.");
        }

        // If the message is not a command, just pass it through
        if (!isServerAction(clientMessage)) {
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

    /**
     * Checks if a client message represents a server action.
     * This is used when deciding whether to send a raw string or a translated command.
     *
     * @param clientMessage the client message to check.
     * @return {@code true} if the message starts with "/", indicating a server action; {@code false} otherwise.
     *
     * <p>Example:</p>
     * <pre>
     * {@code
     * String input = "/command -key1 value1";
     * boolean isServerAction = translator.isServerAction(input);
     * // Output: true
     * }
     * </pre>
     */
    public boolean isServerAction(String clientMessage) {
        return clientMessage.startsWith("/");
    }

    /**
     * Converts a camelCase string to hyphen-separated uppercase (e.g., "camelCase" to "CAMEL-CASE").
     *
     * @param input the camelCase string to convert.
     * @return a hyphen-separated uppercase string.
     * @throws TranslatorException if the input is null, blank, empty, or contains numbers.
     *
     * <p>Example:</p>
     * <pre>
     * {@code
     * String camelInput = "testCommand";
     * String hyphenatedOutput = translator.camelToHyphenatedCaps(camelInput);
     * // Output: "TEST-COMMAND"
     * }
     * </pre>
     */
    public String camelToHyphenatedCaps(String input) {
        if (input == null) {
            throw new TranslatorException("Command Error: input is null.");
        }
        if (input.isEmpty() || input.isBlank()) {
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
