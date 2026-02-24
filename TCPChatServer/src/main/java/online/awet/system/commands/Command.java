package online.awet.system.commands;

import java.util.Map;
import java.util.Set;

public record Command(String name, Map<String, String> params) {

    public Set<String> paramKeys() {
        return params.keySet();
    }
}
