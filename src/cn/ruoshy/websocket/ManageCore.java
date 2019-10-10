package cn.ruoshy.websocket;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ManageCore implements Runnable {

    private SocketManager socketManager;

    public ManageCore(SocketManager socketManager) {
        this.socketManager = socketManager;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        Map<String, String> message = new HashMap<>();
        message.put("name", "admin");
        // 广播消息
        while (true) {
            message.put("content", scanner.nextLine());
            socketManager.broadcast(JSON.toJSONString(message));
        }
    }
}
