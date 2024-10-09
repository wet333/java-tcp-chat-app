# Guide to Creating and Registering New MessageHandlers

To extend the functionality of the application. You will need to create a new `MessageHandler` implementation. 

Each MessageHandler implementation processes a message from the client by first validating whether it is equipped to handle the specific message type. If the message is accepted, the handler proceeds to execute its designated handling routine.

This guide covers implementing a custom `MessageHandler`, annotating it for automatic registration, and verifying it's successfully loaded by the `MessageHandlerFilterChain`.

### Step 1: Create a New `MessageHandler` Implementation

To create a new `MessageHandler` implementation, start by defining a class that extends `BaseMessageHandler`. Within this class, implement the `accepts` and `handleMessage` methods. The `accepts` method should specify the types of messages the handler will process, while the `handleMessage` method will contain the logic for processing these messages.

The client will reformat commands like `"/help"` (entered by the user) into `"HELP:"` which is more understandable/parsable by the server. If you want to know more, check the `ClientMessageParser` class documentation.

Next, add the `@RegisterMessageHandler` annotation to the class. This annotation ensures that the handler is automatically registered in the `MessageHandlerFilterChain`, allowing it to participate in the message handling workflow.

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

### Step 2: Ensure the Handler is Registered in `MessageHandlerFilterChain`

With the `@RegisterMessageHandler` annotation, the `MessageHandlerFilterChain` will automatically register `HelpCommandHandler` (or any other annotated custom handler).

On start-up the application will execute the `SystemUtils.instantiateClassesAnnotatedBy` method to dynamically load into `MessageHandlerFilterChain` all classes annotated with `@RegisterMessageHandler` in the `online.awet.system.messages.handlers` package by default (or any additional configured package).

### Step 3: Test the New `MessageHandler`

1. **Start the application**:

2. **Send Test Messages**:
   - Send the message `/help` from the client to confirm that `HelpCommandHandler` accepts and processes it.
   - Confirm that the client receives the help message as defined in handleMessage and responds as expected.

### Application Flow for ClientHandlers

The `ClientHandlerThread` initializes `MessageHandlerFilterChain` and processes each message received from a client. Here’s how it flows:

- **Client connects** → `ClientHandlerThread` greets and adds client to broadcast.
- **Client sends a message** → `MessageHandlerFilterChain` checks each registered handler.
- **Message matches a handler** → The handler’s `process` method is invoked, processing the message through `handleMessage`.

By following these steps, you can create new `MessageHandler` implementations that are automatically registered and processed in your chat application, allowing flexible, dynamic message handling for different types of commands or messages.