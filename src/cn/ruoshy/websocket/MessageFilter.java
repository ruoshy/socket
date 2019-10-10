package cn.ruoshy.websocket;

import java.io.*;
import java.util.HashMap;

/**
 * 根据报文过滤请求
 */
public class MessageFilter {

    private String path;

    public void doFilter(InputStream inputStream, OutputStream outputStream) throws Exception {
        // 读取请求头属性
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        HashMap<String, String> headers = new HashMap<>();
        while ((line = br.readLine()) != null && !"".equals(line)) {
            String[] header = line.split(": ");
            if (header.length == 2) {
                headers.put(header[0], header[1]);
            } else {
                String[] parame = line.split(" ");
                if (parame.length > 2) {
                    this.path = parame[1];
                }
            }
        }
        // 验证请求
        if (whether(headers)) {
            throw new Exception("缺少必要的header");
        }
        // 返回请求头
        PrintWriter out = new PrintWriter(outputStream);
        out.println("HTTP/1.1 101 Switching Protocols");
        out.println("Connection: Upgrade");
        out.println("Sec-WebSocket-Accept: " + CodingUtil.encryption(headers.get("Sec-WebSocket-Key")));
        out.println("Upgrade: websocket");
        out.println();
        out.flush();
    }

    /**
     * 确定是否存在对应属性
     *
     * @param headers 请求头属性
     * @return boolean
     */
    public boolean whether(HashMap<String, String> headers) {
        if (!"Upgrade".equals(headers.get("Connection"))) {
            return false;
        }

        if (headers.get("Sec-WebSocket-Accept") == null) {
            return false;
        }

        if (!"websocket".equals(headers.get("Upgrade"))) {
            return false;
        }
        return true;
    }

    public String getPath() {
        return path;
    }

}
