import com.fasterxml.jackson.databind.ObjectMapper;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.videoio.VideoCapture;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;

/**
 * socket客户端
 * Created By 虞嘉俊 195144146@qq.com on 2018/8/16
 */
public class SocketClien {

    static Socket socket = null;
    static String host;
    static Integer port;

    public static InetSocketAddress inetSocketAddress = new InetSocketAddress("195144146.tpddns.cn",9001);

    public static void main(String[] args) throws IOException {
//        Socket socket = new Socket("195144146.tpddns.cn",9001);
//        Socket socket = new Socket("127.0.0.1",9001);
        socket = new Socket();
        socket.setReuseAddress(true);//设置SO_REUSEADDR
//        socket.connect(new InetSocketAddress("127.0.0.1",9001));
        socket.connect(inetSocketAddress);//连接服务器
        System.out.println(InetAddress.getLocalHost().getHostAddress());
        host = InetAddress.getLocalHost().getHostAddress();
        System.out.println(socket.getLocalPort());
        port = socket.getLocalPort();
        new HeartbeatSocket(socket).start(); //启动心跳包
        new RunnableSocket(socket).start(); //启动接受监听

        OutputStream outputStream = socket.getOutputStream();//获取输出流

        /*封装输出内容开始*/
        PackageBean packageBean = new PackageBean();
        packageBean.setUserId(1L);
        packageBean.setType(2);
        packageBean.setIndex(1L);
        packageBean.setTotal(1L);
        SocketConnectionBean socketConnectionBean = new SocketConnectionBean();
        socketConnectionBean.setUserId(1L);
        socketConnectionBean.setRequestUserId(2L);
        packageBean.setContent(socketConnectionBean);
        /*封装输出内容结束*/
        ObjectMapper mapper = new ObjectMapper();
        byte[]  s = mapper.writeValueAsBytes(packageBean);
        outputStream.write(s,0,s.length); //输出
    }

    /**
     * 接收监听
     */
    public static class RunnableSocket extends Thread {

        private Socket socket;

        public RunnableSocket(Socket socket){
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                InputStream inputStream = null;
                inputStream = socket.getInputStream();
                while (true) {
                    byte[] buf = new byte[1024];
                    int len = inputStream.read(buf);
                    ObjectMapper objectMapper = new ObjectMapper();
                    PackageBean packageBean = objectMapper.readValue(buf,PackageBean.class);
                    switch (packageBean.getType()){
                        case 3: //接受到视频请求
                            new RequestSocket(packageBean).run();
                            break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 请求socket连接
     */
    public static class RequestSocket extends Thread {
        private PackageBean packageBean;

        public RequestSocket(PackageBean packageBean){
            this.packageBean = packageBean;
        }

        @Override
        public void run() {
            Socket socket = new Socket();
            try {
                socket.setReuseAddress(true);
                socket.bind(new InetSocketAddress(host,port));
                ObjectMapper objectMapper = new ObjectMapper();
                SocketConnectionBean socketConnectionBean = objectMapper.readValue(packageBean.getContent().toString(),SocketConnectionBean.class);
                socket.connect(new InetSocketAddress(socketConnectionBean.getRequestUserNetAddress(),socketConnectionBean.getRequestUserport()));
                System.out.println("接受到请求连接ip:"+socketConnectionBean.getRequestUserNetAddress()+" port"+socketConnectionBean.getRequestUserport());
                String relativelyPath=System.getProperty("user.dir");
                System.load(relativelyPath+"\\out\\production\\UDP\\video\\opencv_java342.dll");
                VideoCapture cap = new VideoCapture(0);
                if(!cap.isOpened())
                    throw new Exception("打开失败");
                Mat image = new Mat();
                while (cap.read(image)){
                    BufferedImage image1 = (BufferedImage) HighGui.toBufferedImage(image);
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    ImageIO.write(image1,"jpg",outputStream);
                    byte[] ss = outputStream.toByteArray();
                    OutputStream outputStream1 = socket.getOutputStream();
                    outputStream1.write(ss,0,ss.length);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }




    /**
     * 心跳包
     */
    public static class HeartbeatSocket extends Thread {

        private Socket socket;

        public HeartbeatSocket(Socket socket){
            this.socket = socket;
        }

        @Override
        public void run() {
            while (true){
                try {
                    if (socket.isClosed())
                        socket.connect(inetSocketAddress);
                    OutputStream outputStream = socket.getOutputStream();
                    PackageBean packageBean = new PackageBean();
                    packageBean.setType(1);
                    packageBean.setUserId(1L);
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
