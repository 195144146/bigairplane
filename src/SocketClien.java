import jdk.nashorn.internal.objects.annotations.Where;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created By 虞嘉俊 195144146@qq.com on 2018/8/16
 */
public class SocketClien {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("127.0.0.1",9001);
        OutputStream outputStream = socket.getOutputStream();
        String s = "ssssggggg";
        outputStream.write(s.getBytes(),0,s.getBytes().length);
        InputStream inputStream = socket.getInputStream();
        byte[] buf = new byte[1024];

        int len = inputStream.read(buf);
        String text = new String(buf,0,len);
        System.out.println(text);
//        InputStream inputStream = socket.getInputStream();
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    while (true){
//                        inputStream.read();
//
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        };
        socket.close();

    }


}
