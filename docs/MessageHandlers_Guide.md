### Guide: Working with Message Handlers in the Chat Application

In this guide, we’ll explore the `MessageHandler` system used in the chat application, including how it processes messages, handles broadcasts, and allows developers to extend the functionality by adding custom handlers.

### Overview of the Message Handling System

The chat application uses a flexible message handling system based on the `MessageHandler` interface, where different implementations can handle specific types of messages. This modular approach allows you to extend the application with new functionalities by simply creating additional message handlers.

#### Key Components

1. **`MessageHandler` Interface**: This defines the contract for all message handlers in the system, requiring a `process(Session session, String message)` method to process incoming messages.
2. **`BaseMessageHandler` Abstract Class**: Provides a base implementation that simplifies creating message handlers by offering `accepts(String message)` and `handleMessage(Session session, String message)` methods.
3. **`RegisterMessageHandler` Annotation**: Automatically registers any `MessageHandler` implementation annotated with `@RegisterMessageHandler` into the `MessageHandlerFilterChain`.
4. **`MessageHandlerFilterChain` Class**: Acts as a central manager, processing messages by iterating over each registered `MessageHandler` and invoking those that accept the given message.

### How Message Handlers Work in the Application

1. **Message Processing**:
    - The `MessageHandlerFilterChain` class receives each incoming message and loops through all registered handlers.
    - Each handler determines if it should handle the message using its `accepts` method. If `accepts` returns `true`, the handler processes the message by executing its `handleMessage` method.
    - This design enables multiple handlers to process a single message, allowing flexible and diverse responses.

2. **Broadcasting and Session Management**:
    - The application uses the `BroadcastManager` to send messages to all connected clients or to specific clients based on their `Session`.
    - In the example `DefaultHandler`, all messages are broadcast to connected users with the sender's session ID as a prefix.

### Creating a New Message Handler

To add new functionality to the chat application, you can create custom `MessageHandler` classes. Here’s how to implement a new handler that processes commands prefixed with “/help” and provides help information to the client.

#### Step 1: Define the Custom Handler Class

1. Create a new class, e.g., `HelpCommandHandler`, in the `online.awet.system.messages.handlers` package.
2. Extend `BaseMessageHandler` to take advantage of the `accepts` and `handleMessage` methods.
3. Annotate the class with `@RegisterMessageHandler` to ensure it is automatically registered with the `MessageHandlerFilterChain`.

#### Step 2: Implement the Handler Logic

1. Override the `accepts(String message)` method to check if the message begins with `/help`.
2. Override `handleMessage(Session session, String message)` to define the response logic. In this case, send a help message back to the user.

Here’s the code:

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
     * only accepts messages that start with "/help".
     *
     * @param message The client message to be evaluated.
     * @return {@code true} if the message starts with "/help"; otherwise, {@code false}.
     */
    @Override
    public boolean accepts(String message) {
        return message.startsWith("/help");
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

#### Step 3: Verify and Test

1. Start the chat application server.
2. Connect a client and send `/help`. You should receive a response with the list of available commands.

### Adding More Complex Handlers

To further extend the application, you can add handlers that:
- Respond to specific commands, like private messaging (`/whisper <user> <message>`).
- Process structured messages, such as JSON or XML.
- Implement custom logic, such as logging or filtering specific messages.

Each new handler can follow the same structure, defining criteria in `accepts` and implementing behavior in `handleMessage`.
