import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Created By 虞嘉俊 195144146@qq.com on 2018/8/20
 */
public class UdpImaageService {

//    Image image = new BufferedImage();
//
//    public UdpImaageService() {
//        this.setTitle("播放器");
//
//        this.add();
//    }

    public static void main(String[] args) throws IOException {
        Frame frame = new ImageFrame();
    }

    public static class ImageFrame extends Frame{
        public ImageFrame(){
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
            RunnableSocket rs = new RunnableSocket(panel,this);
            rs.run();
        }

    }

    public static class RunnableSocket implements Runnable {

        private ImagePanel panel;

        private ImageFrame frame;

        public RunnableSocket(ImagePanel panel,ImageFrame frame){
            this.panel = panel;
            this.frame = frame;
        }

        @Override
        public void run() {
            DatagramSocket datagramSocket = null;
            try {
                datagramSocket = new DatagramSocket(10000);
                byte[] buf = new byte[100000];
                DatagramPacket datagramPacket = new DatagramPacket(buf,buf.length);
                boolean s = true;
                while (true) {
                    datagramSocket.receive(datagramPacket);
//                    System.out.println(datagramPacket.getAddress().getHostAddress());
                    byte[] imageByte = datagramPacket.getData();
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageByte);
                    BufferedImage bufferedImage = ImageIO.read(byteArrayInputStream);
                    byteArrayInputStream.close();
//                    frame.remove(panel);
                    Image image = bufferedImage.getScaledInstance(800,600,16);
                    panel.imagePanel(image);
                    frame.add(panel);
                }
            } catch (Exception e){
                datagramSocket.close();

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

        public void paint(Graphics g){
            g.drawImage(screenImage,0,0,null);
        }
    }

}
