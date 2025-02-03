package online.awet.system.messages.exceptions;

import online.awet.system.messages.core.MessageHandler;

public class MessageHandlerException extends RuntimeException {
    public MessageHandlerException(String message, MessageHandler handler) {
        super(handler.getClass().getName() + ": " + message + "\n");
    }
}