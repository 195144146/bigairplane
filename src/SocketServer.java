import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.deploy.panel.WinUpdatePanel;

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
            new RunnableSocket(socket).start();
        }
    }

    /**
     * Socket服务处理线程
     */
    public static class RunnableSocket extends Thread {

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
                while (true){
                    int len = inputStream.read(buf);
                    //转换为String
                    String text = new String(buf,0,len);
                    //判断是否为连接数据
                    ObjectMapper mapper = new ObjectMapper();
                    PackageBean packageBean = mapper.readValue(buf,PackageBean.class);
                    switch (packageBean.getType()){
                        case 1:
                            serviceHeartBeat(packageBean.getUserId(),socket);// 心跳
                            break;
                        case 2:
                            serviceRequestSocket(packageBean,socket); //请求连接
                            break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        /**
         * 心跳处理
         */
        private void serviceHeartBeat(Long userId, Socket socket){
            SocketServer.socketMap.put(userId,socket);
            System.out.print("接受到心跳来源 ip:"+socket.getInetAddress().getHostAddress());
            System.out.println(" port:"+socket.getPort());
        }

        /**
         * 请求连接socket
         */
        private void serviceRequestSocket(PackageBean packageBean,Socket socket){
            String ip = socket.getInetAddress().getHostAddress(); //获取ip
            Integer port = socket.getPort(); //获取端口
            System.out.print("ip:"+ip);
            System.out.print(" port:"+port);
            System.out.print("请求连接");
            Long userId = packageBean.getUserId();//获取用户id
            serviceHeartBeat(userId,socket);//设置有效
            ObjectMapper objectMapper = new ObjectMapper();
            SocketConnectionBean socketConnectionBean = null;
            try {
                socketConnectionBean = objectMapper.readValue(objectMapper.writeValueAsString(packageBean.getContent()),SocketConnectionBean.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(SocketServer.socketMap.containsKey(socketConnectionBean.getRequestUserId())){
                try {
                    Socket requestSocket = SocketServer.socketMap.get(socketConnectionBean.getRequestUserId());
                    OutputStream outputStream = socket.getOutputStream(); //请求socket输出流
                    /*封装输出内容开始*/
                    socketConnectionBean.setRequestUserNetAddress(requestSocket.getInetAddress().getHostAddress());  //对方地址ip
                    System.out.print("目标ip:"+requestSocket.getInetAddress().getHostAddress());
                    socketConnectionBean.setRequestUserport(requestSocket.getPort()); //对方端口
                    System.out.println(" port:"+requestSocket.getPort());
                    socketConnectionBean.setStatus(true); //可以请求
                    socketConnectionBean.setUserId(0L); //服务器
                    PackageBean packageBean2 = new PackageBean();
                    packageBean2.setUserId(0L); //服务器
                    packageBean2.setType(3);//返回请求连接
                    packageBean2.setIndex(1L);
                    packageBean2.setTotal(1L);
                    packageBean2.setContent(socketConnectionBean);
                    /*输出内容封装完毕*/
                    outputStream.write(objectMapper.writeValueAsBytes(packageBean2)); //输出到请求用户
                    OutputStream outputStream2 = requestSocket.getOutputStream(); //被请求socket输出流
                    /*封装输出内容开始*/
                    SocketConnectionBean socketConnectionBean2 = new SocketConnectionBean();
                    socketConnectionBean2.setRequestUserId(userId); //请求人
                    socketConnectionBean2.setRequestUserNetAddress(ip); //请求地址
                    socketConnectionBean2.setRequestUserport(port); //请求端口
                    socketConnectionBean2.setStatus(true); //可以请求
                    socketConnectionBean2.setUserId(0L); //服务器
                    PackageBean packageBean3 = new PackageBean();
                    packageBean3.setUserId(0L); //服务器
                    packageBean3.setType(3);//返回请求连接
                    packageBean3.setIndex(1L);
                    packageBean3.setTotal(1L);
                    /*输出内容封装完毕*/
                    outputStream2.write(objectMapper.writeValueAsBytes(packageBean3));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                try {
                    System.out.println("无目标信息");
                    OutputStream outputStream = socket.getOutputStream(); //请求socket输出流
                    SocketConnectionBean socketConnectionBean2 = new SocketConnectionBean();
                    socketConnectionBean2.setStatus(false); //可以请求
                    socketConnectionBean2.setUserId(0L); //服务器
                    PackageBean packageBean2 = new PackageBean();
                    packageBean2.setUserId(0L); //服务器
                    packageBean2.setType(3);//返回请求连接
                    packageBean2.setIndex(1L);
                    packageBean2.setTotal(1L);
                    /*输出内容封装完毕*/
                    outputStream.write(objectMapper.writeValueAsBytes(packageBean2));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
