import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ThreadedSocketServer extends Thread {
    private Socket connection;

    public ThreadedSocketServer(Socket socket) {
        connection = socket;
    }

    public void run() {
        try{
            InputStreamReader readConnection = new InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(readConnection);
            PrintWriter writer = new PrintWriter(connection.getOutputStream(), true);

            writer.println("Hi, you have connected to the server");
            writer.println("Write an equation and I'll calculate it for you, separate by space (e.g: 5 + 5). Exit with enter-click");

            //  Receives data from client
            String input = reader.readLine();
            while (input != null) {
                System.out.println("A client wrote: " + input);
                writer.println(input + " = " + calc(input));
                input = reader.readLine();
            }
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    public int calc(String equation) {
        String[] parts = equation.split(" ");
        int firstNumber = Integer.parseInt(parts[0]);
        String symbol = parts[1];
        int secondNumber = Integer.parseInt(parts[2]);

        switch(symbol){

            case "+":
                return firstNumber + secondNumber;

            case "-":
                return firstNumber - secondNumber;

            case "*":
                return firstNumber * secondNumber;
                
            case "/":
                if(secondNumber == 0){
                    System.out.println("Å dele på null er tull!");
                }
                return firstNumber / secondNumber;

            default:
                return 0;
        }
    }

    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(1250);
            System.out.println("Log for server. Now we wait...");
            Scanner scanner = new Scanner(System.in);
            String nextLine = "";
            
            while(!nextLine.trim().equals("q")) {
                System.out.println("Waiting for client, write 'q' to stop server.");
                Socket connection = server.accept();
                Thread t = new ThreadedSocketServer(connection);
                t.start();
                if(scanner.hasNext()) {
                    nextLine = scanner.next();
                }
            }

            server.close();
            scanner.close();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}
