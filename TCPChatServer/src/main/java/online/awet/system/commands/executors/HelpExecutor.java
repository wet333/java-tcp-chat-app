package online.awet.system.commands.executors;

import online.awet.commons.Command;
import online.awet.commons.CommandExecutor;
import online.awet.commons.CommandExecutorPool;
import online.awet.commons.CommandSignature;
import online.awet.commons.CommandTarget;
import online.awet.commons.CommandType;
import online.awet.system.core.ClientConnection;
import online.awet.system.core.ConnectionRegistry;

import java.util.Map;
import java.util.Set;

public class HelpExecutor implements CommandExecutor {

    @Override
    public CommandSignature getSignature() {
        return new CommandSignature(CommandType.HELP, Set.of());
    }

    @Override
    public void execute(Command command) {
        if (ClientConnection.currentConnection() == null) return;

        StringBuilder helpText = new StringBuilder("Commands:");
        for (CommandExecutor executor : CommandExecutorPool.getInstance().getAll()) {
            if (executor instanceof HelpProvider hp) {
                helpText.append("\n  ").append(hp.getDescription());
            }
        }

        ConnectionRegistry.getInstance().sendToCurrentConnection(Command.of(CommandType.PRINT_MSG, CommandTarget.CLIENT,
            Map.of("msg", helpText.toString())));
    }
}
