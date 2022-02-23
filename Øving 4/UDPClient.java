import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class UDPClient {
    DatagramSocket ds = new DatagramSocket();
    InetAddress inetAddress = InetAddress.getLocalHost();
    byte[] byteArr = new byte[1024];
    byte[] byteSend;
    DatagramPacket dpReceive = new DatagramPacket(byteArr, byteArr.length);
    DatagramPacket dpSend;
    Scanner scNr = new Scanner(System.in);

    public UDPClient() throws SocketException, UnknownHostException {
    }

    public void start() throws IOException {
        System.out.println("________CALCULATOR________\nWrite an equation and it will be calculated for you, separate by space (e.g: '5 + 5'). \nTo exit write: 'q'");
        String nextLine = scNr.nextLine();
        while (true) { 
            if (nextLine.equals("q")){
                break;
            }

            if(!equationCorrect(nextLine)) {
                System.out.println("Equation was not entered correctly, please try again:");
                scNr.nextLine();
                continue;
            }
            
            byteSend = nextLine.getBytes();
            dpSend = new DatagramPacket(byteSend, byteSend.length, inetAddress, 1001);
            ds.send(dpSend);

            ds.receive(dpReceive);
            System.out.println("Recieved: " + (new String(dpReceive.getData(), 0, dpReceive.getLength())));
            nextLine = scNr.nextLine();
        }
    }

    private boolean equationCorrect(String nextLine) {
        String[] parts = nextLine.split(" ");
        if(!parts[0].matches("^[0-9]*$")) return false;
        if(!parts[1].matches("[(+=*/)-]")) return false;
        if(!parts[2].matches("^[0-9]*$")) return false;
        return true;
    }

    public void close() {
        scNr.close();
        ds.close();
    }

    public static void main(String[] args) throws IOException {
        UDPClient client = new UDPClient();
        client.start();
        client.close();
    }
}
