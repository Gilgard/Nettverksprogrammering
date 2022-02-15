import java.io.*;
import java.net.*;

public class SocketServer {
    public static void main(String[] args) {
        try{
            int PORT_NUMBER = 1250;
            ServerSocket server = new ServerSocket(PORT_NUMBER);
            System.out.println("Log for serverside. Now we wait...");
            Socket connection = server.accept(); 
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
            
            server.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private static int calc(String equation) {
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
}
