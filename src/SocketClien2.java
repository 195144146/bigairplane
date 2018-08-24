import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created By 虞嘉俊 195144146@qq.com on 2018/8/23
 */
public class SocketClien2 {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("122.234.156.129", 50305);
        OutputStream outputStream = socket.getOutputStream();
        String s = "请求连接";
        outputStream.write(s.getBytes(), 0, s.getBytes().length);
        InputStream inputStream = socket.getInputStream();
        byte[] buf = new byte[1024];
        int len = inputStream.read(buf);
        String text = new String(buf, 0, len);
        System.out.println(text);
    }
}
