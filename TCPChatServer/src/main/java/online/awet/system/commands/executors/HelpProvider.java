package online.awet.system.commands.executors;

/**
 * Optional interface for {@link online.awet.commons.CommandExecutor} implementations
 * that want to appear in the output of the {@code /help} command.
 */
public interface HelpProvider {
    String getHelp();
    String getDescription();
}
