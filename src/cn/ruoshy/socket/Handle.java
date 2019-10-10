package cn.ruoshy.socket;

import java.io.*;

/**
 * web请求处理
 */
public class Handle implements Runnable {
    private java.net.Socket socket;
    private String classPath;

    public Handle(java.net.Socket socket, String classPath) {
        this.socket = socket;
        this.classPath = classPath;
    }

    @Override
    public void run() {
        try (InputStream inputStream = socket.getInputStream();
             OutputStream outputStream = socket.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            PrintWriter out = new PrintWriter(outputStream, true);
            // 请求类型
            String method = null;
            // 请求头属性
            String[] headers;
            // 请求地址
            String path = null;
            // 接收请求头属性
            String line;
            while ((line = br.readLine()) != null && !"".equals(line)) {
                if (method == null) {
                    headers = line.split(" ");
                    method = headers[0];
                    path = headers[1];
                }
            }
            // 处理路径
            if (path != null) {
                // 请求地址处理
                String[] pathVariable = path.split("/");
                if (pathVariable.length == 0) {
                    out.println("HTTP/1.1 200 OK");
                    out.println();
                    out.println("<font style=\"color:red;font-size:50\">Hello world!</font>");
                    return;
                }
                // 请求静态文件处理
                String lastRoute = pathVariable[pathVariable.length - 1];
                if (lastRoute.contains(".")) {
                    // 处理静态文件
                    if (Main.fileType.contains(lastRoute.split("[.]")[1])) {
                        File file = new File(classPath + lastRoute);
                        if (file.exists()) {
                            try (FileInputStream fis = new FileInputStream(file)) {
                                this.socket.getOutputStream().write(fis.readAllBytes());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return;
                        }
                    }
                }
                // 404
                out.println("<font style=\"font-size:50\">404</font>");
            }
        } catch (IOException e) {
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
