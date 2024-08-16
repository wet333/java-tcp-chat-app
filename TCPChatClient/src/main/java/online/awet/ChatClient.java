package online.awet;

import online.awet.system.Connector;
import online.awet.threads.TcpReceiverThread;
import online.awet.threads.UserInterfaceThread;

public class ChatClient {
    public static void main(String[] args) {
        try {
            // Fist, connect to the server, otherwise the app will throw ConnectorException
            Connector.getInstance().connect();

            Thread tcpRecieverThread = new Thread(TcpReceiverThread.getInstance());
            Thread userInterfaceThread = new Thread(UserInterfaceThread.getInstance());

            tcpRecieverThread.start();
            userInterfaceThread.start();

            tcpRecieverThread.join();
            userInterfaceThread.join();

        } catch (InterruptedException e) {
            System.out.println("Connection interrupted, thread malfunctioned");
        }
    }
}