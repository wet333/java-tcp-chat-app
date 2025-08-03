package online.awet.system.messages.core;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface RegisterMessageHandler {
    int priority() default 100;
}
