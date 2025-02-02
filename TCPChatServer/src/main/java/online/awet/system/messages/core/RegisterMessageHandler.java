package online.awet.system.messages.core;

import online.awet.system.messages.handlers.DefaultHandler;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation for automatic registration of a {@link MessageHandler} implementation in the
 * {@link MessageHandlerFilterChain}. Annotated classes are detected and loaded at startup.
 *
 * <p>
 * Handlers are sorted by the {@code priority} value—higher values execute earlier.
 * For example, a handler with a priority of 200 executes before one with 100.
 * </p>
 *
 * <pre>
 * {@literal @}RegisterMessageHandler(priority = 150)
 * public class TextMessageHandler implements MessageHandler {
 *     // Handler implementation
 * }
 * </pre>
 *
 * @see MessageHandlerFilterChain
 * @see MessageHandler
 * @see DefaultHandler
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface RegisterMessageHandler {
    int priority() default 100; // Default priority is 100 if not specified
}
