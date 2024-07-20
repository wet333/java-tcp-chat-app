package online.awet;

import online.awet.actions.lib.AbstractAction;
import online.awet.actions.lib.ActionFactory;
import online.awet.actions.collection.userManagement.RegisterAction;
import online.awet.server.BroadcastManager;
import online.awet.users.AccountsManager;
import online.awet.users.Guest;

import java.io.*;
import java.net.Socket;
import java.util.List;

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
        ActionFactory actionFactory = ActionFactory.getInstance();

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
                // Here the app should interpret the message and search for an action to perform

                AbstractAction registerAction = actionFactory.getAction(RegisterAction.class);
                if (registerAction.isTriggeredByServerMessage(line)) {
                    // Get the data from the message
                    List<String> parts = List.of(line.split(":"));
                    System.out.println("Registering user: " + parts.get(2) + " with password: " + parts.get(3));
                    AccountsManager.getInstance().addAccount(parts.get(2), parts.get(3));

                    // Respond to the client, but don't echo the request
                    broadcastManager.broadcast("User " + parts.get(2) + " has joined the community.", this);
                    socketWriter.newLine();
                    socketWriter.flush();
                    continue;
                }
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
