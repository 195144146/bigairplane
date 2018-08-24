import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * socket客户端
 * Created By 虞嘉俊 195144146@qq.com on 2018/8/16
 */
public class SocketClien {
    public static void main(String[] args) throws IOException {
//        Socket socket = new Socket("195144146.tpddns.cn",9001);
//        Socket socket = new Socket("127.0.0.1",9001);
        Socket socket = new Socket();
        socket.setReuseAddress(true);
//        socket.connect(new InetSocketAddress("127.0.0.1",9001));
        socket.connect(new InetSocketAddress("195144146.tpddns.cn",9001));
        System.out.println(InetAddress.getLocalHost().getHostAddress());
        System.out.println(socket.getLocalPort());
//        启动心跳包
        new HeartbeatSocket(socket).run();

        OutputStream outputStream = socket.getOutputStream();
        PackageBean packageBean = new PackageBean();
        packageBean.setUserId(1L);
        packageBean.setType(2);
        packageBean.setIndex(1L);
        packageBean.setTotal(1L);
        SocketConnectionBean socketConnectionBean = new SocketConnectionBean();
        socketConnectionBean.setUserId(1L);
        socketConnectionBean.setRequestUserId(2L);
        packageBean.setContent(socketConnectionBean);
        ObjectMapper mapper = new ObjectMapper();
        byte[]  s = mapper.writeValueAsBytes(packageBean);
        outputStream.write(s,0,s.length);
        InputStream inputStream = socket.getInputStream();
        byte[] buf = new byte[1024];
        int len = inputStream.read(buf);
        String text = new String(buf,0,len);
        System.out.println(text);

//        开启服务
        if(text.equals("ip:192.168.0.1,port:511235")) {
            ServerSocket serverSocket = new ServerSocket();
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(9001));
            new SocketServer.RunnableSocket(serverSocket.accept()).run();
        }
        socket.close();

    }

    public static class RunnableSocket implements Runnable {

        private Socket socket;

        public RunnableSocket(Socket socket){
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    InputStream inputStream = null;
                    inputStream = socket.getInputStream();
                    byte[] buf = new byte[1024];
                    int len = inputStream.read(buf);
                    String text = new String(buf, 0, len);
                    System.out.println(text);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 心跳包
     */
    public static class HeartbeatSocket implements Runnable {
        private Socket socket;

        public HeartbeatSocket(Socket socket){
            this.socket = socket;
        }

        @Override
        public void run() {
            while (true){
                try {
                    OutputStream outputStream = socket.getOutputStream();
                    PackageBean packageBean = new PackageBean();
                    packageBean.setType(1);
                    packageBean.setUserId(1);
                    ObjectMapper json = new ObjectMapper();
                    String jsonString = json.writeValueAsString(packageBean);
                    outputStream.write(jsonString.getBytes());
                    Thread.sleep(60*1000);  //1分钟执行一次
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        socket.close();
                    } catch (IOException e1) {
                        System.out.println("socket关闭"+e1.getMessage());
                    }
                }
            }
        }
    }


}
