package online.awet;

import online.awet.configurations.Configuration;
import online.awet.system.Connector;
import online.awet.threads.TcpReceiverThread;
import online.awet.tui.ChatTUI;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ChatClient {

    private final Connector connector;
    private ChatTUI tui;
    private Thread receiverThread;

    public ChatClient() {
        this.connector = Connector.getInstance();
    }

    public void start() throws IOException {
        Socket socket = connector.connect();
        BufferedWriter serverWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        TcpReceiverThread receiver = TcpReceiverThread.getInstance();
        receiverThread = new Thread(receiver, "tcp-receiver");
        receiverThread.setDaemon(true);
        receiverThread.start();

        tui = new ChatTUI(serverWriter, receiver.getMessageQueue());
        tui.start();
    }

    public void shutdown() {
        if (tui != null) {
            tui.shutdown();
        }
        if (receiverThread != null) {
            receiverThread.interrupt();
        }
    }

    public static void main(String[] args) {
        Configuration config = Configuration.getInstance();

        if (args.length >= 1) {
            config.setServerIp(args[0]);
        }
        if (args.length >= 2) {
            config.setPort(Integer.parseInt(args[1]));
        }

        ChatClient client = new ChatClient();
        try {
            client.start();
        } catch (IOException e) {
            System.out.println("Could not connect to server: " + e.getMessage());
        } finally {
            client.shutdown();
        }
    }
}
