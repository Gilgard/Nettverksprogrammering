import java.io.IOException;
import java.net.*;

public class UDPMultiServer extends Thread{
    DatagramSocket ds = new DatagramSocket(1001);
    byte[] bytArr = new byte[1024];
    DatagramPacket dpReceive = new DatagramPacket(bytArr, bytArr.length);
    DatagramPacket dpSend;
    InetAddress inetAddress = InetAddress.getLocalHost();

    public UDPMultiServer() throws SocketException, UnknownHostException {
    }


    public void run() {
        try {
            System.out.println("Server booting...");
            String received;

            while (true) {

                ds.receive(dpReceive);
                received = (new String(dpReceive.getData(), 0, dpReceive.getLength()));

                System.out.println("Number received " + received);

                String solved = received + " = " + calculate(received);
                byte[] send = solved.getBytes();

                dpSend = new DatagramPacket(send, send.length, inetAddress, dpReceive.getPort());
                ds.send(dpSend);
            }

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public String calculate(String received){
        try{
            String[] parts = received.split(" ");
            int firstNumber = Integer.parseInt(parts[0]);
            String symbol = parts[1];
            int secondNumber = Integer.parseInt(parts[2]);
        
            switch(symbol){
                case "+":
                    return (firstNumber + secondNumber) + "";

                case "-":
                    return (firstNumber - secondNumber) + "";

                case "*":
                    return (firstNumber * secondNumber) + "";
                    
                case "/":
                    if(secondNumber == 0){
                        return "nAn";
                    }
                    return ((double)firstNumber / (double)secondNumber) + "";

                default:
                    return "Something went wrong in the calculation process.";
            }

        }catch (Exception e) {
            return "Invalid input.";
        }
    }

    public static void main(String[] args) throws IOException {
        UDPMultiServer server = new UDPMultiServer();
        server.start();
    }
}
