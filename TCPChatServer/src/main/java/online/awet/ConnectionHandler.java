package online.awet;

import java.io.*;
import java.net.Socket;

public class ConnectionHandler implements Runnable {

    private final Socket socket;
    private boolean greetUser;

    public ConnectionHandler(Socket socket) {
        this.socket = socket;
        this.greetUser = true;
    }

    public void run() {
        // Get IO
        try {
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));

            if (greetUser) {
                System.out.println("New client connected " + socket.getRemoteSocketAddress() + " !!!");
                writer.write("Server: You have successfully connected to the server.");
                writer.newLine();
                writer.flush();
                greetUser = false;
            }

            String line;

            while ( (line = reader.readLine()) != null) {
                System.out.println(socket.getInetAddress().getHostAddress() + ": " + line);
                writer.write("Sent: " + line + "\n");
                writer.newLine();
                writer.flush();
            }

            System.out.println("Client " + socket.getInetAddress().getHostAddress() + " has disconnected.");
            socket.close();
        } catch (IOException e) {
            System.out.println("Error while handling connection IO");
        }
    }
}
