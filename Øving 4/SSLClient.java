import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLSocketFactory;


public class SSLClient {

    static final int port = 1001;

    public static void main(String[] args) {

        System.setProperty("javax.net.ssl.trustStore", "./keys/keystore.jts");
        System.setProperty("javax.net.ssl.trustStorePassword", "kenrobin");
        SSLSocketFactory sslSocketFactory =
                (SSLSocketFactory)SSLSocketFactory.getDefault();
        try {
            Socket socket = sslSocketFactory.createSocket("localhost", port);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            try (BufferedReader bufferedReader =
                         new BufferedReader(
                                 new InputStreamReader(socket.getInputStream()))) {
                Scanner scanner = new Scanner(System.in);
                while(true){
                    System.out.println("Enter something:");
                    String inputLine = scanner.nextLine();
                    if(inputLine.equals("q")){
                        break;
                    }

                    out.println(inputLine);
                    System.out.println(bufferedReader.readLine());
                }
                scanner.close();
            }

        } catch (IOException ex) {
            Logger.getLogger(SSLClient.class.getName())
                    .log(Level.SEVERE, null, ex);
        }

    }

}