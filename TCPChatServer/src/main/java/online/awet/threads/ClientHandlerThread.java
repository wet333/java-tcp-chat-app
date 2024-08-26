package online.awet.threads;

import online.awet.system.broadcast.BroadcastManager;
import online.awet.system.messages.MessageHandlerChain;
import online.awet.system.sessions.GuestSession;
import online.awet.system.sessions.Session;

import java.io.*;
import java.net.Socket;

public class ClientHandlerThread implements Runnable {

    private final Socket socket;
    private boolean greetUser;
    private final Session session;

    public ClientHandlerThread(Socket socket) {
        this.socket = socket;
        this.greetUser = true;
        this.session = new GuestSession();
    }

    public void run() {
        BroadcastManager broadcastManager = BroadcastManager.getInstance();
        MessageHandlerChain messageHandlerChain = MessageHandlerChain.getInstance();

        try {
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            BufferedWriter socketWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

            broadcastManager.addBroadcastMember(session, socketWriter);

            if (greetUser) {
                System.out.println("New client connected " + socket.getRemoteSocketAddress() + ", with sessionId: " + session.getSessionId());
                broadcastManager.sendTo("You have successfully connected to the server.", session);
                greetUser = false;
            }

            String clientMessage;
            while ((clientMessage = reader.readLine()) != null) {
                messageHandlerChain.process(session, clientMessage);
            }

            String disconnectedMsg = "Client " + socket.getInetAddress().getHostAddress() + " has disconnected.";
            broadcastManager.serverBroadcast(disconnectedMsg);
            System.out.println(disconnectedMsg);

            broadcastManager.removeBroadcastMember(session);
            socket.close();

        } catch (IOException e) {
            System.out.println("Error while handling connection IO");
        }
    }
}
