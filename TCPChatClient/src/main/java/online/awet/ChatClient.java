package online.awet;

import online.awet.system.Connector;
import online.awet.threads.ClientTerminal;
import online.awet.threads.TcpReceiverThread;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    public static void main(String[] args) {

        try {
            Socket socket = Connector.getInstance().connect();
            Scanner scanner = new Scanner(System.in);

            OutputStream out = socket.getOutputStream();

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));

            // TODO: Send client id to server, so the server could use the name
            // Use the Writer to send user data

            Thread tcpRecieverThread = new Thread(TcpReceiverThread.getInstance());
            Thread clientTerminalThread = new Thread(new ClientTerminal(socket, writer, scanner));

            tcpRecieverThread.start();
            clientTerminalThread.start();

            tcpRecieverThread.join();
            clientTerminalThread.join();

        } catch (IOException e) {
            if ("Socket closed".equals(e.getMessage())) {
                System.out.println("Connection closed");
            } else {
                System.out.println("Error while stablishing connection to " + args[0] + " server.");
                System.out.println("Cause: " + e.getMessage());
            }
        } catch (InterruptedException e) {
            System.out.println("Connection interrupted, thread malfunctioned");
        }

    }
}