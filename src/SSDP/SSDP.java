package SSDP;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created By 虞嘉俊 195144146@qq.com on 2018/9/11
 */
public class SSDP {

    /**
     * 根据ssdp服务协议测试ssdp发现
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        Socket socket = new Socket();
        socket.connect(new InetSocketAddress("239.255.255.255",1900));
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("M-SEARCH * HTTP/1.1\r\n");
        stringBuffer.append("HOST: 239.255.255.255:1900\r\n");
        stringBuffer.append("MAN: \"ssdp:discover\"\r\n");
        stringBuffer.append("MX: 5\r\n");
        stringBuffer.append("ST: upnp:rootdevice\r\n");
        System.out.println(stringBuffer.toString());
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(stringBuffer.toString().getBytes(),0,stringBuffer.toString().getBytes().length);
        InputStream inputStream = socket.getInputStream();
        byte[] buf = new byte[4096];
        int len = inputStream.read(buf);
        String content = new String(buf);
        System.out.println(content);





    }

}
