import java.io.*;
import java.net.*;

public class WebServer extends Thread {
    private Socket connection;

    public WebServer(Socket connection) {
        this.connection = connection;
    }

    public void run() {
        try {
            InputStreamReader readConnection = new InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(readConnection);
            
            PrintWriter writer = new PrintWriter(connection.getOutputStream(), true);
        
            writer.println("HTTP/1.0 200 OK");
            writer.println("Content-Type: text/html; charset=utf-8");
            writer.println("");
            writer.println("<!DOCTYPE html>");
            writer.println("<html>");
            writer.println("<body>");
            writer.println("<h1>Welcome to this webserver</h1>");
            writer.println("<h3>These are your headers:</h3>");
            writer.println("<ul>");
            reader.lines().filter( line -> !line.isEmpty())
                            .forEach( line -> writer.printf("<li>%s</li>", line));
            writer.println("</ul>");
            writer.println("</body>");
            writer.println("</html>");
            writer.flush();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        int PORT_NUMBER = 1250;
        ServerSocket server;
        try {
            server = new ServerSocket(PORT_NUMBER);
            System.out.println("Log for server. Now we wait...");

            while(true) {
                Socket connection = server.accept();
                System.out.println("Thread waiting for client");
                WebServer t = new WebServer(connection);
                t.start();
            }
            
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
