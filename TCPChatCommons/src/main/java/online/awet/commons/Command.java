package online.awet.commons;

import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;

public class Command implements Serializable {

    private CommandType type;
    private CommandTarget target;
    private Map<String, String> params;
    private Map<String, String> metadata;

    public Command(CommandType type, CommandTarget target, Map<String, String> params, Map<String, String> metadata) {
        this.type = type;
        this.target = target;
        this.params = params;
        this.metadata = metadata;
    }

    /**
     * Factory for commands with an empty mutable metadata map.
     * Callers should use this instead of the full constructor when metadata is not needed upfront,
     * so that processors can stamp contextual data (e.g. connectionId) into it later.
     */
    public static Command of(CommandType type, CommandTarget target, Map<String, String> params) {
        return new Command(type, target, params, new HashMap<>());
    }

    public CommandType getType() {
        return type;
    }

    public CommandTarget getTarget() {
        return target;
    }

    public Map<String, String> getParams() {
        return params != null ? params : new HashMap<>();
    }

    public Map<String, String> getMetadata() {
        return metadata != null ? metadata : new HashMap<>();
    }

    public void setType(CommandType type) {
        this.type = type;
    }

    public void setTarget(CommandTarget target) {
        this.target = target;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        return "Command{" +
                "type=" + type +
                ", target=" + target +
                ", params=" + params +
                ", metadata=" + metadata +
                '}';
    }
}