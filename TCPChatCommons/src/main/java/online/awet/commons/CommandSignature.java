package online.awet.commons;

import java.util.Objects;
import java.util.Set;

public class CommandSignature {
    
    private final CommandType type;
    private final Set<String> paramKeys;

    public CommandSignature(CommandType type, Set<String> paramKeys) {
        this.type = type;
        this.paramKeys = paramKeys;
    }

    public static CommandSignature of (Command command) {
        return new CommandSignature(command.getType(), command.getParams().keySet());
    }

    public CommandType getType() {
        return type;
    }

    public Set<String> getParamKeys() {
        return paramKeys;
    }

    @Override
    public String toString() {
        return type.name() + " " + paramKeys.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommandSignature that = (CommandSignature) o;
        return type == that.type && paramKeys.equals(that.paramKeys);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(type, paramKeys);
    }
}