package online.awet.system;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.security.KeyStore;
import java.util.Properties;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;

import online.awet.configurations.Configuration;

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
        try {
            Properties tlsProps = new Properties();
            try (InputStream propsStream = getClass().getResourceAsStream("/tls/tls.properties")) {
                tlsProps.load(propsStream);
            }
            char[] password = tlsProps.getProperty("truststore.password").toCharArray();

            KeyStore ts = KeyStore.getInstance("JKS");
            try (InputStream tsStream = getClass().getResourceAsStream("/tls/client.truststore.jks")) {
                ts.load(tsStream, password);
            }

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(ts);

            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, tmf.getTrustManagers(), null);

            SSLSocket server = (SSLSocket) ctx.getSocketFactory()
                    .createSocket(configuration.getServerIp(), configuration.getPort());
            setServerSocket(server);
            return server;
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException("Failed to establish TLS connection: " + e.getMessage(), e);
        }
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
