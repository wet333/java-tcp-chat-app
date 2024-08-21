package online.awet.threads;

import online.awet.system.broadcast.BroadcastManager;
import online.awet.system.sessions.users.Guest;

import java.io.*;
import java.net.Socket;

public class ClientHandlerThread implements Runnable {

    private final Socket socket;
    private boolean greetUser;
    private final String guestId;

    private BufferedWriter socketWriter;

    public ClientHandlerThread(Socket socket) {
        this.socket = socket;
        this.greetUser = true;
        this.guestId = Guest.autogenerateGuestId();
    }

    public void run() {
        BroadcastManager broadcastManager = BroadcastManager.getInstance();

        // Get IO
        try {
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();

            // TODO: I should subscribe to the broadcaster in here and not in the main class.
            // I need to subscribe the socket output stream to the broadcaster, an make it
            // hold a list of output streams for each client connected.

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            socketWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

            if (greetUser) {
                System.out.println("New client connected " + socket.getRemoteSocketAddress() + " !!!");
                socketWriter.write("Server: You have successfully connected to the server.");
                socketWriter.newLine();
                socketWriter.flush();
                greetUser = false;
            }

            // TODO: make a class to handle input parsing and filtering, so i can easily handle multiple message kinds
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(socket.getInetAddress().getHostAddress() + ": " + line);

                // Here the app should interpret the message and search for an action to perform

                broadcastManager.broadcast(guestId + ": " + line + "\n", this);
                socketWriter.newLine();
                socketWriter.flush();
            }

            System.out.println("Client " + socket.getInetAddress().getHostAddress() + " has disconnected.");
            socket.close();
            broadcastManager.removeBroadcastMember(this);
        } catch (IOException e) {
            System.out.println("Error while handling connection IO");
        }
    }

    // TODO: This shouldn't be in the thread class, it has nothing to do with. Move to the broadcaster
    public void sendMessageToClient(String message) {
        try {
            socketWriter.write(message);
            socketWriter.newLine();
            socketWriter.flush();
        } catch (IOException e) {
            System.out.println("Error while sending broadcast message to client " + socket.getInetAddress().getHostAddress());
            System.out.println("Cause: " + e.getMessage());
        }
    }
}
