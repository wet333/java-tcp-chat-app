package online.awet.threads;

import online.awet.system.broadcast.BroadcastManager;
import online.awet.system.messages.core.MessageHandlerFilterChain;
import online.awet.system.sessions.Session;
import online.awet.system.sessions.UserSession;

import java.io.*;
import java.net.Socket;

/**
 * ClientHandlerThread is responsible for managing communication between the server and a single client.
 * Each client connection is handled in a separate instance of this thread, enabling concurrent communication
 * between the server and multiple clients.
 *
 * <p>
 * This thread integrates with several key components of the application:
 * <ul>
 *     <li>{@link BroadcastManager} - Handles message broadcasting to all connected clients.</li>
 *     <li>{@link MessageHandlerFilterChain} - Manages message processing and routing.</li>
 *     <li>{@link Session} - Maintains client-specific session data, enabling tracking of individual clients.</li>
 * </ul>
 * </p>
 *
 * <p>
 * This class runs continuously while the client remains connected, reading messages from the client,
 * processing them, and sending responses as needed.
 * </p>
 *
 * @see BroadcastManager
 * @see MessageHandlerFilterChain
 * @see Session
 */
public class ClientHandlerThread implements Runnable {

    /**
     * The client's socket, which is used for reading from and writing to the client.
     */
    private final Socket socket;

    /**
     * Flag to indicate if the user should be greeted upon connection.
     */
    private boolean greetUser;

    /**
     * Session object representing the client's session information.
     */
    private final Session session;

    /**
     * <p>Initializes a new ClientHandlerThread with the specified client connection socket.</p>
     * <p>By default, a new guest session is assigned to this client connection.</p>
     *
     * @param socket The socket for communicating with the client.
     */
    public ClientHandlerThread(Socket socket) {
        this.socket = socket;
        this.greetUser = true;
        this.session = new UserSession();
    }

    public void run() {
        // Obtain instances of broadcast and message handling services.
        BroadcastManager broadcastManager = BroadcastManager.getInstance();
        MessageHandlerFilterChain messageHandlerFilterChain = MessageHandlerFilterChain.getInstance();

        try {
            // Initialize streams for reading from and writing to the client.
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            BufferedWriter socketWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

            // Add the client session to the broadcast manager, enabling broadcasting to this client.
            broadcastManager.addBroadcastMember(session, socketWriter);

            // Greet the client upon connection and notify other users.
            if (greetUser) {
                System.out.println("New client connected " + socket.getRemoteSocketAddress() + ", with sessionId: " + session.getSessionId());
                broadcastManager.serverDirectMessage("You have successfully connected to the server.", session);
                greetUser = false;
            }

            // Continuously read and process messages from the client until they disconnect.
            String clientMessage;
            while ((clientMessage = reader.readLine()) != null) {
                System.out.println("Msg: " + clientMessage);
                // Process each client message through the message handler chain (MessageHandlerFilterChain).
                messageHandlerFilterChain.process(session, clientMessage);
            }

            // Notify all clients of the disconnection event.
            String disconnectedMsg = "Client " + socket.getInetAddress().getHostAddress() + " has disconnected.";
            broadcastManager.serverBroadcast(disconnectedMsg);
            System.out.println(disconnectedMsg);

            // Remove the client session from the broadcast manager and close the socket.
            broadcastManager.removeBroadcastMember(session);
            socket.close();

        } catch (IOException e) {
            // Handle exceptions related to input/output and socket communication.
            System.out.println("Error while handling connection IO");
        }
    }
}
