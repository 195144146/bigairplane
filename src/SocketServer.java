import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Created By 虞嘉俊 195144146@qq.com on 2018/8/16
 */
public class SocketServer {

    /**
     * 连接Socket列表
     */
    public static final Map<Long,Socket> socketMap = new HashMap<Long,Socket>();

    public static void main(String[] args) throws IOException {
        //创建socket服务端
        ServerSocket serverSocket = new ServerSocket();
        //设置SO_REUSEADDR
        serverSocket.setReuseAddress(true);
        //绑定监听端口9001
        serverSocket.bind(new InetSocketAddress(9001));
        while (true){
            ///监听连接获取socket
            Socket socket = serverSocket.accept();
            //对socket进行操作
            new RunnableSocket(socket).run();
        }
    }

    public static class RunnableSocket implements Runnable {

        private Socket socket;

        public RunnableSocket(Socket socket){
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                //创建输入流
                InputStream inputStream = null;
                //获取输入流
                inputStream = socket.getInputStream();
                //创建缓冲区
                byte[] buf = new byte[1024];
                //读取流中数据 阻塞式
                int len = inputStream.read(buf);
                //转换为String
                String text = new String(buf,0,len);
                //判断是否为连接数据
                ObjectMapper mapper = new ObjectMapper();
                PackageBean packageBean = mapper.readValue(buf,PackageBean.class);
                if (packageBean.getType() == 2){
                   String ip = socket.getInetAddress().getHostAddress(); //获取ip
                   Integer port = socket.getPort(); //获取端口
                   System.out.println(ip);
                   System.out.println(port);
                   Long userId = packageBean.getUserId();//获取用户id
                   SocketServer.socketMap.put(userId,socket);//设置有效
                   SocketConnectionBean socketConnectionBean = (SocketConnectionBean)packageBean.getContent();
                   if(SocketServer.socketMap.containsKey(socketConnectionBean.getRequestUserId())){

                   }
                    OutputStream outputStream = socket.getOutputStream();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
