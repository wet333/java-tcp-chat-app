package online.awet.commons;

/**
 * Interface that defines a class that has an executable procedure for a specific command, this logic its executable ONLY by a Command with the same signature.
 */
public interface CommandExecutor {

    /**
     * Executes the given command.
     * @param command The command to execute.
     */
    void execute(Command command);

    /**
     * Returns the signature of the command that this executor can execute.
     * @return The signature of the command.
     */
    CommandSignature getSignature();
}
