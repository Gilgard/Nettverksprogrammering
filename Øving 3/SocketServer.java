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
                double result = (double) calc(input);

                if(result == Integer.MAX_VALUE) {
                    System.out.println("The equation could not be read.");
                    writer.println("The equation could not be read.");
                }else if(result == Integer.MIN_VALUE) {
                    System.out.println("Dividing by zero is not possible.");
                    writer.println("Dividing by zero is not possible.");
                }else {
                    writer.println(input + " = " + result);
                }
                input = reader.readLine();
            }
            
            server.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private static double calc(String equation) {
        String[] parts = equation.split(" ");
        double firstNumber = (double) Integer.parseInt(parts[0]);
        String symbol = parts[1];
        double secondNumber = (double) Integer.parseInt(parts[2]);
        switch(symbol){
            case "+":
                return firstNumber + secondNumber;

            case "-":
                return firstNumber - secondNumber;

            case "*":
                return firstNumber * secondNumber;
                
            case "/":
                if(secondNumber == 0){
                    return Integer.MIN_VALUE;
                }
                return firstNumber / secondNumber;

            default:
                return Integer.MAX_VALUE;
        }
    }
}
