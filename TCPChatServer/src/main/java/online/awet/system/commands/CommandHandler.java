package online.awet.system.commands;

import online.awet.system.core.broadcast.ClientConnection;

import java.util.Set;

public interface CommandHandler {

    Set<CommandSignature> getSignatures();

    void handle(ClientConnection connection, Command command);
}
