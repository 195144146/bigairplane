
import jdk.nashorn.internal.objects.annotations.Where;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Created By 虞嘉俊 195144146@qq.com on 2018/8/16
 */
public class Service {

    public static void main(String[] args) throws IOException {
        DatagramSocket datagramSocket = new DatagramSocket(10000);

        byte[] buf = new byte[1024];
        DatagramPacket datagramPacket = new DatagramPacket(buf,buf.length);
        while (true) {
            datagramSocket.receive(datagramPacket);
            System.out.println(datagramPacket.getAddress().getHostAddress());
            System.out.println(new String(datagramPacket.getData()));
        }
    }

}
