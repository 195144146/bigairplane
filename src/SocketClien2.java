import com.fasterxml.jackson.databind.ObjectMapper;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created By 虞嘉俊 195144146@qq.com on 2018/8/23
 */
public class SocketClien2 {


    static String host;
    static Integer port;

    public static InetSocketAddress inetSocketAddress = new InetSocketAddress("195144146.tpddns.cn",9001);

    public static class ImageFrame extends Frame{
        static Socket socket = null;

        public ImageFrame(){
            try {
                this.setTitle("ImageFrame");
                ImagePanel panel = new ImagePanel(null);
                this.add(panel);
                this.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        System.exit(0);
                        super.windowClosing(e);
                    }
                });
                this.pack();
                this.setVisible(true);
                socket = new Socket();
                socket.setReuseAddress(true);//设置SO_REUSEADDR
    //        socket.connect(new InetSocketAddress("127.0.0.1",9001));
                socket.connect(inetSocketAddress);//连接服务器
                System.out.println(InetAddress.getLocalHost().getHostAddress());
                host = InetAddress.getLocalHost().getHostAddress();
                System.out.println(socket.getLocalPort());
                port = socket.getLocalPort();
                new HeartbeatSocket(socket).start(); //启动心跳包
                new RunnableSocket(socket,panel,this).start(); //启动接受监听
            } catch (Exception e){

            }
        }

    }

    public static class ImagePanel extends Panel{
        private final Image screenImage = new BufferedImage(800,600,2);

        private final Graphics2D screenGraphic = (Graphics2D)screenImage.getGraphics();

        private Image backgroundImage;

        public ImagePanel(Image image){
            loadImage(image);
            setFocusable(true);
            setPreferredSize(new Dimension(800,600));
            drawView();
        }

        public void imagePanel(Image image){
            loadImage(image);
            setFocusable(true);
            setPreferredSize(new Dimension(800,600));
            drawView();
        }

        public void loadImage(Image image){
            if (image != null) {
                backgroundImage = image;
            }else{
                ImageIcon icon = new ImageIcon(getClass().getResource("\\video\\timg.jpg"));
                backgroundImage = icon.getImage();
            }
        }

        public void drawView(){
            screenGraphic.drawImage(backgroundImage,0,0,null);
        }

        @Override
        public void paint(Graphics g){
            g.drawImage(screenImage,0,0,null);
        }
    }

    public static void main(String[] args) throws IOException {
        Frame frame = new ImageFrame();
//        Socket socket = new Socket("195144146.tpddns.cn",9001);
//        Socket socket = new Socket("127.0.0.1",9001);

    }

    /**
     * 接收监听
     */
    public static class RunnableSocket extends Thread {

        private Socket socket;
        private ImagePanel panel;
        private ImageFrame frame;

        public RunnableSocket(Socket socket,ImagePanel panel,ImageFrame frame){
            this.socket = socket;
            this.panel = panel;
            this.frame = frame;
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
                            new RequestSocket(packageBean,panel,frame).run();
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
        private ImagePanel panel;
        private ImageFrame frame;

        public RequestSocket(PackageBean packageBean, ImagePanel panel, ImageFrame frame){
            this.packageBean = packageBean;
            this.panel = panel;
            this.frame = frame;
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
//                String relativelyPath=System.getProperty("user.dir");
//                System.load(relativelyPath+"\\out\\production\\UDP\\video\\opencv_java342.dll");
//                VideoCapture cap = new VideoCapture(0);
//                if(!cap.isOpened())
//                    throw new Exception("打开失败");
//                Mat image = new Mat();
//                while (cap.read(image)){
//                    BufferedImage image1 = (BufferedImage) HighGui.toBufferedImage(image);
//                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//                    ImageIO.write(image1,"jpg",outputStream);
//                    byte[] ss = outputStream.toByteArray();
//                    OutputStream outputStream1 = socket.getOutputStream();
//                    outputStream1.write(ss,0,ss.length);
//                }
                InputStream inputStream = socket.getInputStream();
                byte[] buf = new byte[1024];
                System.out.println("开始接受信息");
                //读取流中数据 阻塞式
                while (true) {
                    int len = inputStream.read(buf);
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buf);
                    BufferedImage bufferedImage = ImageIO.read(byteArrayInputStream);
                    byteArrayInputStream.close();
                    Image image = bufferedImage.getScaledInstance(800,600,16);
                    panel.imagePanel(image);
                    frame.add(panel);
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
                    packageBean.setUserId(2L);
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
