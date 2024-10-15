# Guide for Creating and Registering New MessageHandlers

This guide covers the implementation of a custom `MessageHandler` in order to add new features, and then verifying it's up and running.

The `MessageHandlerFilterChain` intercepts each message sent by the client to the server and sequentially checks each registered MessageHandler to see if it `accepts` the message. Once a handler `accepts` the message, the `MessageHandlerFilterChain` stops further checks, allowing the first accepted handler to execute its designated handling routine.

> Take a look into `Configurations.ALLOW_MULTIPLE_MESSAGE_HANDLERS` for allowing multiple handlers per message.

<hr>

### Creating a New `MessageHandler` Implementation

To create a new `MessageHandler` implementation, start by defining a class that extends `BaseMessageHandler`. Within this class, implement the `accepts` and `handleMessage` methods. 

The `accepts` method will check the message sent by the client and decide whether it's valid or not. In case of returning `true`, the `handleMessage` method will be executed.

> Please make sure to check the client's message (temporarily logging it to the console/terminal), as the client applies certain transformations to messages before sending them to the server side.
> 
> The client will reformat commands like `"/help"` (entered by the user) into `"HELP:"` which is more understandable/parsable by the server. If you want to know more, check the `ClientMessageParser` class documentation.

Next, ensure the handler is registered in `MessageHandlerFilterChain` adding the `@RegisterMessageHandler` annotation at the start of the class declaration, this will allow it to participate in the message handling pipeline.

On start-up, the application will dynamically load into `MessageHandlerFilterChain` all classes annotated with `@RegisterMessageHandler` in the `online.awet.system.messages.handlers` package by default (or any additional configured package).

#### Example: Creating a `HelpCommandHandler`

```java
package online.awet.system.messages.handlers;

import online.awet.system.broadcast.BroadcastManager;
import online.awet.system.messages.BaseMessageHandler;
import online.awet.system.messages.RegisterMessageHandler;
import online.awet.system.sessions.Session;

@RegisterMessageHandler
public class HelpCommandHandler extends BaseMessageHandler {

   /**
    * Checks if this handler should process the message. This handler
    * only accepts messages that start with "HELP:".
    *
    * @param message The client message to be evaluated.
    * @return {@code true} if the message starts with "HELP:"; otherwise, {@code false}.
    */
   @Override
   public boolean accepts(String message) {
      return message.startsWith("HELP:");
   }

   /**
    * Processes the help command by sending a help message back to the requesting client.
    *
    * @param session The session associated with the client sending the message.
    * @param message The client message to be processed.
    */
   @Override
   public void handleMessage(Session session, String message) {
      BroadcastManager broadcastManager = BroadcastManager.getInstance();

      // Sample help information to send back to the client
      String helpMessage = "Available commands:\n" +
              "/help - Display available commands\n" +
              "/whisper <user> <message> - Send a private message\n" +
              "/quit - Disconnect from the chat\n";

      broadcastManager.serverDirectMessage(helpMessage, session);
   }
}
```

<hr>

### Testing our new Handler

1. **Start the application**:

2. **Send Test Messages**:
   - Send the message `/help` from the client to confirm that `HelpCommandHandler` accepts and processes it.
   - Confirm that the client receives the help message as defined in handleMessage and responds as expected.

### Application Flow for Messages

The `ClientHandlerThread` initializes `MessageHandlerFilterChain` and processes each message received from a client. Here’s how it flows:

- **Client connects** → `ClientHandlerThread` greets and adds client to broadcast.
- **Client sends a message** → `MessageHandlerFilterChain` checks if any registered handler accepts the message.
- **Message matches a handler** → The handler’s `handleMessage` method is executed.

By following these steps, you can create new `MessageHandler` implementations that are automatically registered and processed in your chat application, allowing flexible, dynamic message handling for different types of commands or messages.