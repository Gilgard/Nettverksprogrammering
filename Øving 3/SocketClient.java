import java.io.*;
import java.net.*;
import java.util.Scanner;

public class SocketClient {
    public static void main(String[] args) {
        int PORT_NUMBER = 1250;
        Scanner readFromCommandLine = new Scanner(System.in);
        System.out.print("Type host of server: ");
        String clientMachine = readFromCommandLine.nextLine();

        // Setup connection to server
        try{
            Socket connection = new Socket(clientMachine, PORT_NUMBER);
            InputStreamReader readConnection = new InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(readConnection);
            PrintWriter writer = new PrintWriter(connection.getOutputStream(), true);
            
            System.out.println("Connection was created");
            // Reads info from server and prints it
            String introMessage1 = reader.readLine();
            String introMessage2 = reader.readLine();
            System.out.println(introMessage1 + "\n" + introMessage2);

            // Reads input from commandline (the user)
            String oneLine = readFromCommandLine.nextLine();
            while (oneLine != null) {
                if(oneLine.equals("")){
                    System.out.println("Exiting program.");
                    break;
                }
                writer.println(oneLine);
                String response = reader.readLine();
                System.out.println("From server: " + response);
                oneLine = readFromCommandLine.nextLine();
            }
            connection.close();
        }catch(Exception e) {
            e.printStackTrace();
        }
        readFromCommandLine.close();
    }
}
