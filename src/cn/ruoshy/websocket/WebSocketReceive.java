package cn.ruoshy.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class WebSocketReceive implements Runnable {

    private InputStream inputStream;
    private SocketManager socketManager;

    public WebSocketReceive(InputStream inputStream, SocketManager socketManager) {
        this.inputStream = inputStream;
        this.socketManager = socketManager;
    }

    @Override
    public void run() {
        while (true) {
            try {
                receive();
            } catch (Exception e) {
                break;
            }
        }
    }

    public void receive() throws IOException {
        byte[] frameReader = inputStream.readNBytes(1);
        // FIN表示这是消息中的最后一个片段
        // 当FIN为0时表示不是最后一个片段，为1时表示最后一个片段
        int fin = CodingUtil.getByteBit(frameReader[0], 0);
        // RSV 是保留的扩展定义位，没有扩展的情况下为0
        int rsv1 = CodingUtil.getByteBit(frameReader[0], 1);
        int rsv2 = CodingUtil.getByteBit(frameReader[0], 2);
        int rsv3 = CodingUtil.getByteBit(frameReader[0], 3);

        // opcode操作码
        int opcode = CodingUtil.getByteBits(frameReader[0], 4, 7);
        switch (opcode) {
            case 0:
                // 连续帧
                break;
            case 1:
                // 文本帧
                break;
            case 2:
                // 二进制帧
                break;
            case 8:
                // 连接关闭
                throw new IOException("socket close");
            case 9:
                // ping
                break;
            case 10:
                // pone
                break;
        }
        // mask掩码
        frameReader = inputStream.readNBytes(1);
        int mask = CodingUtil.getByteBit(frameReader[0], 0);

        // payload len 有效载荷
        int payloadLen = CodingUtil.getByteBits(frameReader[0], 1, 7);

        // extended payload length 有效载荷长度延长
        long extendedPayloadLen = payloadLen;
        if (payloadLen == 126) {
            // 读2个字节
            extendedPayloadLen = CodingUtil.bytesToLong(inputStream.readNBytes(2));
        } else if (payloadLen == 127) {
            // 读8个字节
            extendedPayloadLen = CodingUtil.bytesToLong(inputStream.readNBytes(8));
        }

        // 获得屏蔽键
        byte[] maskingKey = null;
        if (mask == 1) {
            maskingKey = inputStream.readNBytes(4);
        }

        // 解码
        frameReader = inputStream.readNBytes(Long.valueOf(extendedPayloadLen).intValue());
        if (maskingKey != null) {
            byte[] encodeBytes = new byte[frameReader.length];
            for (int i = 0; i < encodeBytes.length; i++) {
                encodeBytes[i] = (byte) (frameReader[i] ^ maskingKey[i % 4]);
            }
            String message = new String(encodeBytes);
            socketManager.broadcast(message);
            System.out.println(message);
            // 自定义的消息格式 JSON解析使用了alibaba的fastjson
            // JSONObject messager = JSON.parseObject(message);
            // System.out.println(messager.get("name") + " : " + messager.get("content"));
        }

    }

}
