package online.awet.system.commands.handlers;

import online.awet.system.broadcast.BroadcastManager;
import online.awet.system.commands.*;
import online.awet.system.commands.handlers.extensions.HelpProvider;
import online.awet.system.core.SystemUtils;
import online.awet.system.sessions.Session;
import online.awet.system.sessions.holder.SessionHolder;

import java.util.Map;
import java.util.Set;

@RegisterCommandHandler
public class HelpHandler implements CommandHandler {

    @Override
    public Set<CommandSignature> getSignatures() {
        return Set.of(CommandSignature.of("HELP"));
    }

    @Override
    public void handle(SessionHolder sessionHolder, Command command) {
        BroadcastManager broadcastManager = BroadcastManager.getInstance();
        Session session = sessionHolder.getCurrentSession();

        Map<Class<? extends CommandHandler>, CommandHandler> handlers =
            SystemUtils.instantiateClassesAnnotatedBy(
                CommandHandler.class,
                "RegisterCommandHandler",
                "online.awet.system.commands.handlers"
            );

        broadcastManager.serverDirectMessage("Commands:", session);
        for (CommandHandler handler : handlers.values()) {
            if (handler instanceof HelpProvider helpProvider) {
                broadcastManager.serverDirectMessage("  " + helpProvider.getDescription(), session);
            }
        }
    }
}
