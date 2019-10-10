package cn.ruoshy.websocket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class WebSocketService {

    private SocketManager socketManager;

    public WebSocketService() {
        this.socketManager = new SocketManager();
    }

    /**
     * 监听指定端口 默认80
     * 等待客户端连接
     * 连接后创建一个线程进行处理
     *
     * @return WebSocketService
     */
    public WebSocketService start(int port) {
        new Thread(new ManageCore(socketManager)).start();
        try {
            ServerSocket server = new ServerSocket(port);
            while (true) {
                Socket client = server.accept();
                new Thread(new SocketHandle(client, socketManager)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }
}
