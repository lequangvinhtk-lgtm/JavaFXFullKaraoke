package com.vku.karaoke.server;

import com.vku.karaoke.model.SystemConfig;
import com.vku.karaoke.server.network.ClientHandler;
import com.vku.karaoke.utils.ConfigXMLUtil;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainServer {
    public static void main(String[] args) {
        SystemConfig config = ConfigXMLUtil.loadConfig();
        ExecutorService pool = Executors.newCachedThreadPool();
        // ExecutorService (bộ quản lý luồng) giúp server xử lý nhiều client cùng lúc.
        // newCachedThreadPool() tự tạo thêm luồng khi có client mới.

        try (ServerSocket serverSocket = new ServerSocket(config.getServerPort())) {
            System.out.println("=================================================");
            System.out.println(" KARAOKE SERVER ĐANG CHẠY Ở PORT: " + config.getServerPort());
            System.out.println(" Mỗi client sẽ được xử lý bằng một luồng riêng.");
            System.out.println("=================================================");

            while (true) {

                Socket socket = serverSocket.accept();
                System.out.println("[CONNECT] Client mới: " + socket.getInetAddress());
                pool.execute(new ClientHandler(socket));
                // Đưa client vào ClientHandler để xử lý trên một luồng riêng.
            }
        } catch (Exception e) {
            System.err.println("[SERVER ERROR] Không thể khởi động server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
                 /*
============================================================
MAIN SERVER - ÁP DỤNG NETWORKING + MULTITHREADING
============================================================

Class này là chương trình server chính.

Kiến thức áp dụng:
1. Networking (lập trình mạng):
   - ServerSocket: mở cổng server để chờ client kết nối.
   - Socket: kết nối giữa client và server.

2. Multithreading (đa luồng):
   - ExecutorService: quản lý nhiều luồng.
   - Mỗi client được xử lý bởi một ClientHandler riêng.

Luồng chạy:
1. Server đọc config.xml để lấy serverPort.
2. Server tạo ServerSocket ở port đó.
3. Server chạy vòng lặp while(true) để chờ client.
4. Khi client kết nối, server accept() ra một Socket.
5. Server đưa Socket đó vào ClientHandler.
6. ExecutorService chạy ClientHandler trên một luồng riêng.

Câu trả lời khi thầy hỏi:
"Em áp dụng đa luồng ở server. Mỗi client kết nối vào sẽ được xử lý bằng một ClientHandler riêng thông qua ExecutorService, nên nhiều người dùng có thể truy cập cùng lúc."
*/