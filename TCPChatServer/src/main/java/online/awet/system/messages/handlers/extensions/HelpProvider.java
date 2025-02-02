package online.awet.system.messages.handlers.extensions;

/**
 * The HelpProvider interface provides methods that allow the application
 * to offer help and suggestions about the MessageHandler to the user.
 *
 * @see online.awet.system.messages.handlers.HelpCommandHandler HelpCommandHandler
 */
public interface HelpProvider {

    /**
     * Returns a help text describing the usage of the implementing class functionality.
     *
     * @return a string on how to use the Command/Handler.
     */
    String getHelp();


    /**
     * Returns a line of text, describing the Command/Handler basic functions.
     * <p>
     *     Example: <code>/myCommand: do something useful</code>
     * </p>
     *
     * @return a string with the name and a short description.
     */
    String getDescription();
}