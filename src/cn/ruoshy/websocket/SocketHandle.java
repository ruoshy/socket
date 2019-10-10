package cn.ruoshy.websocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * WebSocket通信服务
 * 处理WebSocket报文
 * 开通数据接收线程
 */
public class SocketHandle implements Runnable {

    private Socket socket;
    private SocketManager socketManager;

    /**
     * @param socket 与客户端之间的连接
     */
    public SocketHandle(Socket socket, SocketManager socketManager) {
        this.socket = socket;
        this.socketManager = socketManager;
    }

    @Override
    public void run() {
        try (InputStream inputStream = socket.getInputStream();
             OutputStream outputStream = socket.getOutputStream()) {
            // 处理报文,过滤请求
            MessageFilter messageFilter = new MessageFilter();
            messageFilter.doFilter(inputStream, outputStream);
            String path = messageFilter.getPath();
            // 缓存与客户端的消息发送通道
            socketManager.add(outputStream);
            // 开启接收消息
            Thread receiveThread = new Thread(new WebSocketReceive(inputStream, socketManager));
            receiveThread.start();
            // 发送心跳包
            while (receiveThread.isAlive()) {
                Thread.sleep(15000);
                socketManager.notice("h", outputStream);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (this.socket != null) {
                    this.socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
