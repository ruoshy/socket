package cn.ruoshy.websocket;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.LinkedList;

public class SocketManager {

    private LinkedList<OutputStream> outputStreams;

    public SocketManager() {
        outputStreams = new LinkedList<>();
    }

    /**
     * 添加缓存
     *
     * @param outputStream
     */
    public void add(OutputStream outputStream) {
        outputStreams.add(outputStream);
    }

    /**
     * 删除缓存
     *
     * @param outputStream
     */
    public void remove(OutputStream outputStream) {
        outputStreams.remove(outputStream);
    }

    /**
     * 广播
     *
     * @param message 消息
     */
    public void broadcast(String message) {
        Iterator<OutputStream> iterator = outputStreams.listIterator();
        while (iterator.hasNext()) {
            try {
                OutputStream outputStream = iterator.next();
                push(message.getBytes(), outputStream);
            } catch (IOException e) {
                iterator.remove();
            }
        }
    }

    /**
     * 通知
     *
     * @param message 消息
     */
    public void notice(String message, OutputStream outputStream) {
        try {
            push(message.getBytes(), outputStream);
        } catch (IOException e) {
            remove(outputStream);
        }
    }

    /**
     * 推消息
     *
     * @param bytes
     * @param outputStream
     * @throws IOException
     */
    public void push(byte[] bytes, OutputStream outputStream) throws IOException {
        outputStream.write(new byte[]{(byte) 0x81, (byte) bytes.length});
        outputStream.write(bytes);
    }

    /**
     * 返回当前缓存的连接数
     *
     * @return int
     */
    public int getCurrentConnNum() {
        return outputStreams.size();
    }
}
