package online.awet.system.commands;

import online.awet.system.core.SystemUtils;
import online.awet.system.core.broadcast.ClientConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class CommandRouter {

    private static final Logger logger = LoggerFactory.getLogger(CommandRouter.class);

    private static final String HANDLERS_PACKAGE = "online.awet.system.commands.handlers";

    private final Map<CommandSignature, CommandHandler> handlers = new HashMap<>();
    private final CommandParser parser = new CommandParser();

    public CommandRouter() {
        loadHandlers();
    }

    private void loadHandlers() {
        logger.info("Loading CommandHandlers...");

        Map<Class<? extends CommandHandler>, CommandHandler> discovered = SystemUtils.instantiateClassesAnnotatedBy(
            CommandHandler.class,
            "RegisterCommandHandler",
            HANDLERS_PACKAGE
        );

        for (var entry : discovered.entrySet()) {
            CommandHandler handler = entry.getValue();
            for (CommandSignature signature : handler.getSignatures()) {
                CommandHandler existing = handlers.put(signature, handler);
                if (existing != null) {
                    throw new IllegalStateException(
                        "Duplicate signature " + signature
                        + " registered by " + existing.getClass().getSimpleName()
                        + " and " + handler.getClass().getSimpleName()
                    );
                }
                logger.info("{} ---> {}", signature, handler.getClass().getSimpleName());
            }
        }

        logger.info("Loaded {} signatures from {} handlers", handlers.size(), discovered.size());
    }

    public void route(ClientConnection connection, String rawMessage) {
        Command command;
        try {
            command = parser.parse(rawMessage);
        } catch (CommandParserException e) {
            logger.warn("Parse error: {}", e.getMessage());
            return;
        }

        logger.debug("Received: {}", command);

        CommandSignature signature = new CommandSignature(command.name(), command.paramKeys());
        CommandHandler handler = handlers.get(signature);

        if (handler != null) {
            logger.info("{} <-- {}", handler.getClass().getSimpleName(), signature);
            try {
                handler.handle(connection, command);
            } catch (Exception e) {
                logger.error("Error handling command {}: {}", command.name(), e.getMessage());
            }
        } else {
            logger.warn("No handler for signature: {}", signature);
        }
    }
}
