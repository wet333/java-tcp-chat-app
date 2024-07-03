package online.awet;

import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

public class NetIPAnalyzer {
    public static void main(String[] args) {

        final String dataFilename = "net_data.txt";

        System.out.println("Searching IPs of the following domains: " + System.lineSeparator());
        Arrays.stream(args).iterator().forEachRemaining(System.out::println);

        try {

            FileWriter fileWriter = new FileWriter(dataFilename);
            InetAddress localAddress = InetAddress.getLocalHost();

            fileWriter.write("Current System --> " + localAddress.toString() + System.lineSeparator());
            fileWriter.write(System.lineSeparator());

            for (String domain : args) {
                InetAddress address = InetAddress.getByName(domain);
                fileWriter.write(address.toString() + ", isReachable: " + address.isReachable(1000) + System.lineSeparator());
            }

            fileWriter.flush();

        } catch (UnknownHostException e) {
            System.out.println("Error while gathering network data");
        } catch (IOException e) {
            System.out.println("Error while writing or reading file: " + dataFilename);
        }

    }
}