import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.Buffer;

import com.googlecode.javacv.cpp.opencv_highgui;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.HighGui;
import org.opencv.videoio.VideoCapture;
import javax.imageio.ImageIO;

/**
 * Created By 虞嘉俊 195144146@qq.com on 2018/8/20
 */
public class UdpImageClien {

    public static void main(String[] args)  {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        DatagramSocket datagramSocket = null;
        try {


        String relativelyPath=System.getProperty("user.dir");
        System.load(relativelyPath+"\\out\\production\\UDP\\video\\opencv_java342.dll");
        datagramSocket = new DatagramSocket();
//        VideoCapture cap = new VideoCapture(relativelyPath+"\\out\\production\\UDP\\video\\sss.mp4");
        VideoCapture cap = new VideoCapture(0);
        if(!cap.isOpened())
            throw new Exception("打开失败");
        /*获取总帧数*/
        double framcount = cap.get(opencv_highgui.CV_CAP_PROP_FRAME_COUNT);
        /*获取帧率*/
        double fps = cap.get(opencv_highgui.CV_CAP_PROP_FPS);
        Mat image = new Mat();
        while (cap.read(image)){
            BufferedImage image1 = (BufferedImage)HighGui.toBufferedImage(image);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image1,"jpg",outputStream);
            byte[] ss = outputStream.toByteArray();
            DatagramPacket datagramPacket = new DatagramPacket(ss,ss.length, InetAddress.getByName("195144146.tpddns.cn"),10000);
            datagramSocket.send(datagramPacket);
//            Thread.sleep(30);
        }
        } catch (Exception e) {
            datagramSocket.close();
        }
    }



}
