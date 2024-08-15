package online.awet.configurations;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Configuration {

    private static Configuration instance;
    private Integer port;
    private String serverIp;

    private Configuration() {}

    public static Configuration getInstance() {
        if (instance == null) {
           instance = new Configuration();
        }
        return instance;
    }

    public int getPort() {
        if (port == null) {
            return DefaultConfigurations.DEFAULT_PORT;
        }
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getServerIp() {
        if (serverIp == null) {
            try {
                InetAddress vpsServerIp = InetAddress.getByName("awet.online");
                return vpsServerIp.getHostAddress();
            } catch (UnknownHostException e) {
                System.out.println("Server not found, couldn't reach to awet.online");
            }
        }
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }
}
