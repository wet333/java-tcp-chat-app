package online.awet;

import online.awet.server.BroadcastManager;
import online.awet.users.Guest;

import java.io.*;
import java.net.Socket;

public class ConnectionHandler implements Runnable {

    private final Socket socket;
    private boolean greetUser;
    private final String guestId;

    private BufferedWriter socketWriter;

    public ConnectionHandler(Socket socket) {
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
