package cn.ruoshy.websocket;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class CodingUtil {

    /**
     * 字节数组转长整型
     *
     * @param bytes 字节数组
     * @return Long
     */
    public static long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.put(bytes);
        buffer.flip();
        return buffer.getLong();
    }

    /**
     * 对Sec-WebSocket-Key密钥进行加密
     * 生成Sec-WebSocket-Accept值
     *
     * @param key 密钥
     * @return String
     * @throws NoSuchAlgorithmException
     */
    public static String encryption(String key) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA1");
        md.update((key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes());
        byte[] bytes = md.digest();
        return new String(Base64.getEncoder().encode(bytes));
    }

    /**
     * 取指定位置bit
     *
     * @param b     字节
     * @param index 位置
     * @return
     */
    public static int getByteBit(byte b, int index) {
        return (b & 0x80 >> index) >> (7 - index);
    }

    /**
     * 取指定区间bit
     *
     * @param b     字节
     * @param start 开始位置
     * @param end   结束位置
     * @return int
     */
    public static int getByteBits(byte b, int start, int end) {
        return (b << start & 0xff) >> (7 - end + start);
    }

}
