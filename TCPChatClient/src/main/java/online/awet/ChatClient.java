package online.awet;

import online.awet.threads.ClientTerminal;
import online.awet.threads.ServerPrinter;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    public static void main(String[] args) {

        int port = 7560;

        if (args.length < 1) {
            System.out.println("Usage: java TCPClient <server-ip>");
            return;
        }

        try {
            Socket socket = new Socket(args[0], port);
            Scanner scanner = new Scanner(System.in);

            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));

            Thread serverReaderThread = new Thread(new ServerPrinter(reader));
            Thread clientTerminalThread = new Thread(new ClientTerminal(socket, writer, scanner));

            serverReaderThread.start();
            clientTerminalThread.start();

            serverReaderThread.join();
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