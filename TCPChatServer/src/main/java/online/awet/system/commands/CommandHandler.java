package online.awet.system.commands;

import online.awet.system.sessions.holder.SessionHolder;

import java.util.Set;

public interface CommandHandler {

    Set<CommandSignature> getSignatures();

    void handle(SessionHolder sessionHolder, Command command);
}
