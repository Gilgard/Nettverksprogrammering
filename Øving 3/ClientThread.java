import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientThread extends Thread {
    private Socket connection;

    public ClientThread(Socket socket) {
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
            System.out.println("A client wrote: $input");
            writer.println("$input = ${calc(input)}");
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
    Socket server = new Socket("", 2222);
    
    System.out.println("Log for server. Now we wait...");
    for (int i = 0; i < 5; i++) {
        try {
            System.out.println("Thread nr: ${i + 1} waiting for client");
            Thread t = new ClientThread(server);
            t.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
}
