package online.awet;

import online.awet.system.Connector;
import online.awet.threads.TcpReceiverThread;
import online.awet.threads.UserInterfaceThread;

import java.io.IOException;

public class ChatClient {
    public static void main(String[] args) {
        try {
            Connector.getInstance().connect();

            Thread tcpRecieverThread = new Thread(TcpReceiverThread.getInstance());
            Thread userInterfaceThread = new Thread(UserInterfaceThread.getInstance());

            tcpRecieverThread.start();
            userInterfaceThread.start();

            tcpRecieverThread.join();
            userInterfaceThread.join();

        } catch (IOException e) {
            System.out.println("Could not connect to server: " + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("Connection interrupted, thread malfunctioned");
        }
    }
}