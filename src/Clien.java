import java.io.IOException;
import java.net.*;

public class Clien {

    public static void main(String[] args) throws IOException {
        DatagramSocket datagramSocket = new DatagramSocket();
        String io = "fdsfs";
        DatagramPacket datagramPacket = new DatagramPacket(io.getBytes(),io.getBytes().length, InetAddress.getByName("127.0.0.1"),10000);
        datagramSocket.send(datagramPacket);
        datagramSocket.close();
    }



}
