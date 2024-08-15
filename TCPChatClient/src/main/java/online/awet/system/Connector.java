package online.awet.system;

import online.awet.configurations.Configuration;
import online.awet.configurations.DefaultConfigurations;

import java.io.IOException;
import java.net.Socket;

public class Connector {

    private static Connector instance;

    private Configuration configuration;
    private Socket serverSocket;

    private Connector() {}

    public static Connector getInstance() {
        if (instance == null) {
            instance = new Connector();
            instance.configuration = Configuration.getInstance();
        }
        return instance;
    }

    public Socket connect() {
        return connect("localhost");
    }

    public Socket connect(String serverIp) {
        configuration.setServerIp(serverIp);
        try {
            Socket server = new Socket(configuration.getServerIp(), DefaultConfigurations.DEFAULT_PORT);
            setServerSocket(server);
            return server;
        } catch (IOException e) {
            System.out.println("Couldn't connect to server, with IP: " + configuration.getServerIp());
        }
        return null;
    }

    public Socket getServerSocket() {
        return serverSocket;
    }

    public void setServerSocket(Socket serverSocket) {
        this.serverSocket = serverSocket;
    }
}
