import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created By 虞嘉俊 195144146@qq.com on 2018/8/16
 */
public class SocketServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(9001);
        while (true){
            new RunnableSocket(serverSocket.accept()).run();
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
                InputStream inputStream = null;
                inputStream = socket.getInputStream();
                byte[] buf = new byte[1024];
                int len = inputStream.read(buf);
                String text = new String(buf,0,len);
                System.out.println(socket.getInetAddress().getHostAddress());
                System.out.println(text);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
