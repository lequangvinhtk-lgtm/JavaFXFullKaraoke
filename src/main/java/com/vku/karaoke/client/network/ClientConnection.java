package com.vku.karaoke.client.network;

import com.vku.karaoke.model.Request;
import com.vku.karaoke.model.Response;
import com.vku.karaoke.model.SystemConfig;
import com.vku.karaoke.utils.ConfigXMLUtil;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientConnection {
    private final Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;

    public ClientConnection() throws Exception {
        SystemConfig config = ConfigXMLUtil.loadConfig();

        socket = new Socket(config.getServerHost(), config.getServerPort());
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());
    }

    /*
send() dùng để gửi một Request từ client lên server.

Quy trình:
1. writeObject(request): gửi object Request lên server.
2. readObject(): chờ server xử lý và trả Response.
3. Ép kiểu dữ liệu nhận được về Response.
*/
    public synchronized Response send(Request request) throws Exception {
        out.writeObject(request);
        out.flush();
        out.reset();

        Object response = in.readObject();

        if (!(response instanceof Response)) {
            throw new IllegalStateException("Server trả về dữ liệu không đúng định dạng Response");
        }

        return (Response) response;
    }

    public void close() {
        try {
            in.close();
        } catch (Exception ignored) {
        }

        try {
            out.close();
        } catch (Exception ignored) {
        }

        try {
            socket.close();
        } catch (Exception ignored) {
        }
    }
}

/*
============================================================
CLIENT CONNECTION - KẾT NỐI CLIENT VỚI SERVER
============================================================

Class này nằm ở phía client JavaFX.

Kiến thức áp dụng:
1. Networking bằng Socket.
2. Object Stream để gửi/nhận object.

Luồng chạy:
1. Đọc config.xml để lấy serverHost và serverPort.
2. Tạo Socket kết nối tới server.
3. Tạo ObjectOutputStream để gửi Request.
4. Tạo ObjectInputStream để nhận Response.
5. Hàm send() gửi Request và chờ server trả Response.

Câu trả lời khi thầy hỏi:
"ClientConnection là lớp giúp JavaFX client kết nối tới server. Client gửi Request qua ObjectOutputStream và nhận Response qua ObjectInputStream."
*/
