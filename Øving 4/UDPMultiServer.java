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
            String received = "-1";
            while (!received.isBlank()) {

                ds.receive(dpReceive);
                received = (new String(dpReceive.getData(), 0, dpReceive.getLength()));

                System.out.println("Number received " + received);

                String solved = received + " " + calculate(received);
                byte[] send = solved.getBytes();

                dpSend = new DatagramPacket(send, send.length, inetAddress, dpReceive.getPort());
                ds.send(dpSend);
            }
            ds.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public String calculate(String received){
        String result = "";
        String[] components;

        if (received.contains("-")){
            components = received.split("-");
            result = "" + (Integer.parseInt(components[0])-Integer.parseInt(components[1]));
        }
        else if (received.contains("+"))
        {
            components = received.split("\\+");
            result = "" + (Integer.parseInt(components[0])+Integer.parseInt(components[1]));
        }
        else if (received.contains("*"))
        {
            components = received.split("\\*");
            result = "" + (Integer.parseInt(components[0])*Integer.parseInt(components[1]));
        }
        else if (received.contains("/"))
        {
            components = received.split("/");
            result = "" + (Double.parseDouble(components[0])/Integer.parseInt(components[1]));
        }
        else
        {
            result = "Equation is invalid";
        }
        return result;
    }

    public static void main(String[] args) throws IOException {
        UDPMultiServer server = new UDPMultiServer();
        server.start();
    }
}
