package online.awet.system.commands;

import java.util.Set;

public record CommandSignature(String name, Set<String> paramKeys) {

    public static CommandSignature of(String name, String... keys) {
        return new CommandSignature(name, Set.of(keys));
    }
}
