import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class UDPClient2 {
    DatagramSocket ds = new DatagramSocket();
    InetAddress inetAddress = InetAddress.getLocalHost();
    byte[] byteArr = new byte[1024];
    byte[] byteSend;
    DatagramPacket dpReceive = new DatagramPacket(byteArr, byteArr.length);
    DatagramPacket dpSend;

    public UDPClient2() throws SocketException, UnknownHostException {
    }

    public void start() throws IOException {
        Scanner scNr = new Scanner(System.in);
        int operation = -1;
        while (operation != 0) {
            System.out.println("________CALCULATOR________\n1: Subtraction\n2: Addition\n" +
                    "3: Multiply\n4: Divide\n0: quit");
            operation = scNr.nextInt();
            if (operation == 0){
                byteSend = "".getBytes();
                dpSend = new DatagramPacket(byteSend, byteSend.length, inetAddress, 1001);
                ds.send(dpSend);
            }
            if (!(operation < 0 || operation > 4)){
                System.out.println("Enter first number");
                int first = scNr.nextInt();
                System.out.println("Enter number to equate");
                int last = scNr.nextInt();

                if (operation == 1) {
                    byteSend = (first + "-" + last).getBytes();
                } else if (operation == 2) {
                    byteSend = (first + "+" + last).getBytes();
                } else if (operation == 3) {
                    byteSend = (first + "*" + last).getBytes();
                } else {
                    byteSend = (first + "/" + last).getBytes();
                }
                dpSend = new DatagramPacket(byteSend, byteSend.length, inetAddress, 1001);
                ds.send(dpSend);

                ds.receive(dpReceive);
                System.out.println("The Result is " + (new String(dpReceive.getData(), 0, dpReceive.getLength())));
            }
        }
    }

    public void close() {
        ds.close();
    }

    public static void main(String[] args) throws IOException {
        UDPClient2 client = new UDPClient2();
        client.start();
        client.close();
    }
}
