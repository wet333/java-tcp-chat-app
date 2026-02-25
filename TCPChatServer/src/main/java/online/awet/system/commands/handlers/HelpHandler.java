package online.awet.system.commands.handlers;

import online.awet.system.core.broadcast.ClientConnection;
import online.awet.system.commands.*;
import online.awet.system.commands.handlers.extensions.HelpProvider;
import online.awet.system.core.SystemUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@RegisterCommandHandler
public class HelpHandler implements CommandHandler {

    @Override
    public Set<CommandSignature> getSignatures() {
        return Set.of(CommandSignature.of("HELP"));
    }

    @Override
    public void handle(ClientConnection connection, Command command) {
        Map<Class<? extends CommandHandler>, CommandHandler> handlers =
            SystemUtils.instantiateClassesAnnotatedBy(
                CommandHandler.class,
                "RegisterCommandHandler",
                "online.awet.system.commands.handlers"
            );

        try {
            connection.send("Commands:");
            for (CommandHandler handler : handlers.values()) {
                if (handler instanceof HelpProvider helpProvider) {
                    connection.send("  " + helpProvider.getDescription());
                }
            }
        } catch (IOException ignored) {}
    }
}
