package cn.ruoshy.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

/**
 * 简陋的Web服务器
 */
public class Main {
    // 静态文件位置
    private final String classPath = this.getClass().getClassLoader().getResource("./").getPath();

    // 静态文件类型
    public static Set<String> fileType;

    static {
        fileType = new HashSet<>();
        fileType.add("png");
        fileType.add("ico");
        fileType.add("html");
    }

    public static void main(String[] args) {
        new Main(80);
    }

    public Main(int port) {
        try {
            // 开启服务监听端口
            ServerSocket server = new ServerSocket(port);
            while (true) {
                // 阻塞，等待请求
                Socket client = server.accept();
                // 创建线程处理请求
                new Thread(new Handle(client, classPath)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
