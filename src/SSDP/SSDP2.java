package SSDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created By 虞嘉俊 195144146@qq.com on 2018/9/13
 */
public class SSDP2 {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket();
        //        socket.connect(new InetSocketAddress("192.168.0.185",1900));
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("NOTIFY * HTTP/1.1\r\n");
        stringBuffer.append("HOST: 239.255.255.250:1900\r\n");
        stringBuffer.append("MAN: ssdp:discover\r\n");
        stringBuffer.append("MX: 40\r\n");
        stringBuffer.append("ST: ssdp:all\r\n");
//        stringBuffer.append("NTS: ssdp:alive\r\n");
//        stringBuffer.append("SERVER: Synology/DSM/192.168.0.15\r\n");
//        stringBuffer.append("USN: uuid:73796E6F-6473-6D00-0000-00113278f99::upnp:rootdevice\r\n");
        stringBuffer.append("\r\n");

        System.out.println(stringBuffer.toString());

        DatagramSocket datagramSocket = new DatagramSocket();
        DatagramPacket datagramPacket = new DatagramPacket(stringBuffer.toString().getBytes(), stringBuffer.toString().getBytes().length, InetAddress.getByName("239.255.255.250"), 1900);
        datagramSocket.send(datagramPacket);

        byte[] buf = new byte[100000];
        DatagramPacket datagramPacket2 = new DatagramPacket(buf, buf.length);
        datagramSocket.receive(datagramPacket2);

        byte[] imageByte = datagramPacket2.getData();
        String content = new String(imageByte);
        System.out.println(content);

    }
}
