package online.awet;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

public class URLPageDownloader {

    public static void main(String[] args) {

        String outputFile = "default.html";
        String siteURL = args[0];

        try {
            URL url = URI.create(siteURL).toURL();
            outputFile = url.getAuthority().replaceAll("\\.", "_") + url.getPath() + ".html";

            try (FileWriter out = new FileWriter(outputFile)) {

                URLConnection connection = url.openConnection();
                InputStream webIn = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(webIn));

                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                    out.write(line + System.lineSeparator());
                }
                out.flush();
            } catch (IOException e) {
                System.out.println("An error has occurred while trying to create/write to: " + outputFile);
            }

        } catch (MalformedURLException e) {
            System.out.println("Invalid URL: " + siteURL);
        }
    }

}
