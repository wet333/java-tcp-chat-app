package online.awet.system.messages;

import online.awet.system.messages.handlers.DefaultHandler;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The {@code RegisterMessageHandler} annotation marks a {@link MessageHandler}
 * implementation for automatic registration within the {@link MessageHandlerChain}.
 *
 * <p>
 * When a class implementing {@code MessageHandler} is annotated with
 * {@code RegisterMessageHandler}, the application will detect and register
 * it in the {@code MessageHandlerChain} at startup, allowing it to participate
 * in the processing of client messages.
 * </p>
 *
 * <p>
 * This annotation is retained at runtime to enable reflection-based scanning,
 * ensuring that all annotated handlers are dynamically loaded and made available
 * within the message handling chain without requiring manual registration.
 * </p>
 *
 * <p>Example usage:</p>
 * <pre>
 * {@literal @}RegisterMessageHandler
 * public class TextMessageHandler implements MessageHandler {
 *     // Handler implementation
 * }
 * </pre>
 *
 * @see MessageHandlerChain
 * @see MessageHandler
 * @see DefaultHandler
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface RegisterMessageHandler {
}
