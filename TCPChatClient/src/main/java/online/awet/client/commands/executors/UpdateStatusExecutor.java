package online.awet.client.commands.executors;

import online.awet.commons.Command;
import online.awet.commons.CommandExecutor;
import online.awet.commons.CommandSignature;
import online.awet.commons.CommandType;
import online.awet.system.ClientContext;

import java.util.Set;

/**
 * Handles {@code UPDATE_STATUS} commands from the server by updating the status bar
 * authentication state and triggering a re-render.
 *
 * <p>The server always sends both {@code authenticated} and {@code username} keys.
 * On logout/unregister, {@code username} is an empty string.
 */
public class UpdateStatusExecutor implements CommandExecutor {

    @Override
    public CommandSignature getSignature() {
        return new CommandSignature(CommandType.UPDATE_STATUS, Set.of("authenticated", "username"));
    }

    @Override
    public void execute(Command command) {
        boolean authenticated = Boolean.parseBoolean(command.getParams().get("authenticated"));
        ClientContext ctx = ClientContext.getInstance();

        if (authenticated) {
            String username = command.getParams().get("username");
            ctx.getStatusBarState().setAuthenticated(username);
        } else {
            ctx.getStatusBarState().setUnauthenticated();
        }

        ctx.notifyStateChanged();
    }
}
