package online.awet.system.commands.executors;

import online.awet.commons.Command;
import online.awet.commons.CommandExecutor;
import online.awet.commons.CommandSignature;
import online.awet.commons.CommandTarget;
import online.awet.commons.CommandType;
import online.awet.system.core.ClientConnection;
import online.awet.system.core.ConnectionRegistry;
import online.awet.system.core.sessions.Session;

import java.util.Map;
import java.util.Set;

public class LogoutExecutor implements CommandExecutor, HelpProvider {

    @Override
    public CommandSignature getSignature() {
        return new CommandSignature(CommandType.LOGOUT, Set.of());
    }

    @Override
    public void execute(Command command) {
        ClientConnection connection = ClientConnection.currentConnection();
        if (connection == null) return;

        Session session = connection.getSession();
        ConnectionRegistry registry = ConnectionRegistry.getInstance();

        if (!session.isAuthenticated()) {
            registry.sendToCurrentConnection(Command.of(CommandType.PRINT_MSG, CommandTarget.CLIENT,
                Map.of("msg", "You are not logged in.")));
            return;
        }

        String username = session.getDisplayName();
        connection.logout();

        registry.sendToCurrentConnection(Command.of(CommandType.UPDATE_STATUS, CommandTarget.CLIENT,
            Map.of("authenticated", "false", "username", "")));

        registry.sendToCurrentConnection(Command.of(CommandType.PRINT_MSG, CommandTarget.CLIENT,
            Map.of("msg", "User " + username + " has been logged out.")));
    }

    @Override
    public String getHelp() {
        return "Usage: /logout";
    }

    @Override
    public String getDescription() {
        return "/logout: Allows you to logout from your current session.";
    }
}
