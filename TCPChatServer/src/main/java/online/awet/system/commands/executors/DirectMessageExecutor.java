package online.awet.system.commands.executors;

import online.awet.commons.Command;
import online.awet.commons.CommandExecutor;
import online.awet.commons.CommandSignature;
import online.awet.commons.CommandTarget;
import online.awet.commons.CommandType;
import online.awet.system.core.ClientConnection;
import online.awet.system.core.ConnectionRegistry;

import java.util.Map;
import java.util.Set;

public class DirectMessageExecutor implements CommandExecutor, HelpProvider {

    @Override
    public CommandSignature getSignature() {
        return new CommandSignature(CommandType.SEND_DM, Set.of("username", "msg"));
    }

    @Override
    public void execute(Command command) {
        ClientConnection sender = ClientConnection.currentConnection();
        if (sender == null) return;

        String targetUsername = command.getParams().get("username");
        String msg = command.getParams().get("msg");
        String senderName = sender.getSession().getDisplayName();
        ConnectionRegistry registry = ConnectionRegistry.getInstance();

        if (targetUsername.equals(sender.getSession().getUsername())) {
            registry.sendToCurrentConnection(Command.of(CommandType.PRINT_MSG, CommandTarget.CLIENT,
                Map.of("msg", "You cannot send a private message to yourself.")));
            return;
        }

        Command dm = Command.of(CommandType.DM_MSG, CommandTarget.CLIENT,
            Map.of("sender", senderName, "recipient", targetUsername, "msg", msg));
        boolean delivered = registry.sendToUser(targetUsername, dm);

        if (delivered) {
            registry.sendToCurrentConnection(Command.of(CommandType.DM_MSG, CommandTarget.CLIENT,
                Map.of("sender", senderName, "recipient", targetUsername, "msg", msg)));
        } else {
            registry.sendToCurrentConnection(Command.of(CommandType.PRINT_MSG, CommandTarget.CLIENT,
                Map.of("msg", "User '" + targetUsername + "' is not connected.")));
        }
    }

    @Override
    public String getHelp() {
        return "Usage: /send -username <target> -msg '<message>'";
    }

    @Override
    public String getDescription() {
        return "/send -username -msg '<message>': Send a private message to a specific user.";
    }
}
