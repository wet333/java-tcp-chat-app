package online.awet.system;

import online.awet.configurations.Configuration;

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

    public Socket connect() throws IOException {
        Socket server = new Socket(configuration.getServerIp(), configuration.getPort());
        setServerSocket(server);
        return server;
    }

    public Socket getServerSocket() throws ConnectorException {
        if (serverSocket == null) {
            throw new ConnectorException("You need to connect, after getting server socket.");
        }
        return serverSocket;
    }

    public void setServerSocket(Socket serverSocket) {
        this.serverSocket = serverSocket;
    }
}
