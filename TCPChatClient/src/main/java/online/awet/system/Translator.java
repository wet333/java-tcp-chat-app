package online.awet.system;

import java.util.*;

public class Translator {

    public static Translator instance;

    private final String COMMAND_HEADER_FORMAT = "%s:";
    private final String KEY_VALUE_PAIR_FORMAT = "%s=%s;";

    private Translator() {}

    public static Translator getInstance() {
        if (instance == null) {
            instance = new Translator();
        }
        return instance;
    }

    public String translate(String clientMessage) {

        if (clientMessage == null || clientMessage.isBlank()) {
            throw new TranslatorException("Cannot translate an empty message. Please enter a string value to validate.");
        }

        List<String> messageParts = new ArrayList<>(List.of(clientMessage.split(" ")));
        StringBuilder result = new StringBuilder();

        System.out.println("Parts: " + messageParts);

        String command = messageParts.removeFirst();
        if ("/".equals(command)) {
            throw new TranslatorException("Error, empty command: \"/\"");
        }
        if (command.startsWith("/")) {
            result.append(String.format(COMMAND_HEADER_FORMAT, camelToHyphenatedCaps(command.replace("/", ""))));
        }

        while(!messageParts.isEmpty()) {
            String key = messageParts.removeFirst();
            String value = messageParts.removeFirst();

            if (!key.startsWith("-")) {
                throw new TranslatorException("Invalid message, on: " + clientMessage.replace(key, "<!>" + key + "<!>"));
            }
            key = key.replace("-", "");

            if (value == null) {
                throw new TranslatorException("Missing value on key: " + key);
            }

            result.append(String.format(KEY_VALUE_PAIR_FORMAT, key, value));
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
