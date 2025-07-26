package online.awet.threads;

import online.awet.system.broadcast.BroadcastManager;
import online.awet.system.messages.core.MessageHandlerFilterChain;
import online.awet.system.sessions.Session;
import online.awet.system.sessions.holder.InMemorySessionHolder;
import online.awet.system.sessions.holder.SessionHolder;

import java.io.*;
import java.net.Socket;

public class ClientHandlerThread implements Runnable {

    private final Socket socket;

    private boolean greetUser;

    private final SessionHolder sessionHolder;

    public ClientHandlerThread(Socket socket) {
        this.socket = socket;
        this.greetUser = true;
        this.sessionHolder = new InMemorySessionHolder();
    }

    public void run() {
        BroadcastManager broadcastManager = BroadcastManager.getInstance();
        MessageHandlerFilterChain messageHandlerFilterChain = MessageHandlerFilterChain.getInstance();
        Session session = sessionHolder.getCurrentSession();

        try {
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            BufferedWriter socketWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

            broadcastManager.addBroadcastMember(session, socketWriter);

            if (greetUser) {
                System.out.println("New client connected " + socket.getRemoteSocketAddress() + ", with sessionId: " + session.getSessionId());
                broadcastManager.serverDirectMessage("You have successfully connected to the server.", session);
                greetUser = false;
            }

            String clientMessage;
            while ((clientMessage = reader.readLine()) != null) {
                System.out.println("RAW Client Message: " + clientMessage);
                messageHandlerFilterChain.process(sessionHolder, clientMessage);
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
