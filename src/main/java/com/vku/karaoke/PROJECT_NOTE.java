package com.vku.karaoke;

/*
============================================================
PROJECT NOTE - JAVA FX FULL KARAOKE
============================================================

Đề tài:
Ứng dụng JavaFX quản lý bài hát Karaoke theo mô hình Client - Server.

Các chức năng chính:
1. Đăng nhập / đăng ký tài khoản.
2. Phân quyền Admin / User.
3. Quản lý bài hát: thêm, sửa, xóa, tìm kiếm.
4. Quản lý playlist cá nhân.
5. Lưu lịch sử tìm kiếm.
6. Import / Export bài hát bằng file TXT.
7. Export danh sách bài hát ra XML.
8. Lưu cấu hình hệ thống trong config.xml.
9. Server hỗ trợ nhiều client cùng lúc bằng đa luồng.

============================================================
1. OOP - LẬP TRÌNH HƯỚNG ĐỐI TƯỢNG
============================================================

OOP là cách chia chương trình thành các class và object.

Áp dụng trong bài:
- Song: đại diện cho bài hát.
- User: đại diện cho người dùng.
- Playlist: đại diện cho danh sách phát.
- SearchHistory: đại diện cho lịch sử tìm kiếm.
- Request: dữ liệu client gửi lên server.
- Response: dữ liệu server trả về client.

Lợi ích:
- Dữ liệu được đóng gói rõ ràng.
- Code dễ đọc, dễ bảo trì.
- Dễ truyền object giữa client và server.

Ví dụ:
Client tạo object Song -> gửi trong Request -> Server nhận -> SongDAO lưu vào MySQL.

============================================================
2. MULTITHREADING - ĐA LUỒNG
============================================================

Multithreading là kỹ thuật xử lý nhiều công việc cùng lúc.

Áp dụng trong bài:
- MainServer dùng ExecutorService.
- Mỗi client kết nối vào server sẽ được xử lý bằng một ClientHandler riêng.
- ClientHandler implements Runnable và code xử lý nằm trong hàm run().

Class liên quan:
- server.MainServer
- server.network.ClientHandler

Câu lệnh quan trọng:
ExecutorService pool = Executors.newCachedThreadPool();
pool.execute(new ClientHandler(socket));

Giải thích:
- ExecutorService: bộ quản lý luồng.
- ClientHandler: lớp xử lý một client.
- run(): hàm chạy trong luồng.
- execute(): đưa công việc vào thread pool để chạy.

Lợi ích:
Nhiều người dùng có thể truy cập server cùng lúc mà server không bị treo.

============================================================
3. IOSTREAM - ĐỌC GHI FILE
============================================================

IOStream dùng để đọc và ghi dữ liệu ra file.

Áp dụng trong bài:
- Export danh sách bài hát ra backup_songs.txt.
- Import danh sách bài hát từ backup_songs.txt vào database.

Class liên quan:
- utils.FileIOUtil
- server.network.ClientHandler

Câu lệnh quan trọng:
BufferedWriter writer = Files.newBufferedWriter(...);
BufferedReader reader = Files.newBufferedReader(...);

Giải thích:
- BufferedWriter: ghi file.
- BufferedReader: đọc file.
- readLine(): đọc từng dòng.
- write(): ghi dữ liệu.
- try-with-resources: tự đóng file sau khi dùng.

============================================================
4. XML
============================================================

XML là file lưu dữ liệu bằng các thẻ.

Áp dụng trong bài:
1. config.xml lưu cấu hình server và database.
2. backup_songs.xml lưu danh sách bài hát để sao lưu/chia sẻ.

Class liên quan:
- utils.ConfigXMLUtil
- utils.XMLUtil
- utils.DBUtil
- client.network.ClientConnection

Câu lệnh quan trọng:
DocumentBuilderFactory.newInstance()
Document doc = ...
Element root = doc.createElement(...)
Transformer.transform(...)

Giải thích:
- Document: tài liệu XML.
- Element: thẻ XML.
- parse(): đọc XML.
- Transformer: ghi XML ra file.

============================================================
5. DATABASE / JDBC
============================================================

JDBC là công nghệ giúp Java kết nối và thao tác với database.

Áp dụng trong bài:
Database MySQL lưu:
- users: tài khoản người dùng.
- songs: danh sách bài hát.
- playlists: playlist của user.
- playlist_songs: bài hát thuộc playlist.
- search_history: lịch sử tìm kiếm.

Class liên quan:
- utils.DBUtil
- server.dao.SongDAO
- server.dao.UserDAO
- server.dao.PlaylistDAO
- server.dao.SearchHistoryDAO

Câu lệnh quan trọng:
Connection connection = DriverManager.getConnection(...);
PreparedStatement ps = connection.prepareStatement(sql);
ResultSet rs = ps.executeQuery();
ps.executeUpdate();

Giải thích:
- Connection: kết nối database.
- PreparedStatement: câu SQL có tham số.
- ResultSet: kết quả SELECT.
- executeQuery(): chạy SELECT.
- executeUpdate(): chạy INSERT, UPDATE, DELETE.

============================================================
6. JAVA SECURITY - BẢO MẬT
============================================================

Security trong bài gồm:
- Đăng nhập.
- Đăng ký tài khoản.
- Hash mật khẩu.
- Phân quyền Admin/User.
- Kiểm tra quyền ở server.

Class liên quan:
- utils.PasswordUtil
- server.dao.UserDAO
- model.User
- server.network.ClientHandler
- client.controller.KaraokeDashboardController

Câu lệnh quan trọng:
MessageDigest.getInstance("SHA-256");
currentUser.isAdmin();
requireAdmin();

Giải thích:
- Hash password: băm mật khẩu, không lưu mật khẩu thường.
- Role: vai trò của user, gồm ADMIN và USER.
- requireAdmin(): kiểm tra quyền Admin ở server.

Admin được:
- Thêm bài hát.
- Sửa bài hát.
- Xóa bài hát.
- Import TXT.

User được:
- Xem bài hát.
- Tìm kiếm.
- Tạo playlist.
- Xem lịch sử tìm kiếm.

============================================================
7. NETWORKING - LẬP TRÌNH MẠNG
============================================================

Networking là kỹ thuật giúp client và server giao tiếp qua mạng.

Bài này dùng Socket, không dùng Servlet/Tomcat.

Class liên quan:
- server.MainServer
- client.network.ClientConnection
- server.network.ClientHandler
- model.Request
- model.Response

Câu lệnh quan trọng:
ServerSocket serverSocket = new ServerSocket(port);
Socket socket = serverSocket.accept();
Socket socket = new Socket(host, port);
ObjectOutputStream out = new ObjectOutputStream(...);
ObjectInputStream in = new ObjectInputStream(...);

Giải thích:
- ServerSocket: socket phía server, dùng để chờ client.
- Socket: kết nối giữa client và server.
- ObjectOutputStream: gửi object.
- ObjectInputStream: nhận object.
- Request: yêu cầu client gửi lên.
- Response: phản hồi server trả về.

Servlet là gì?
Servlet là class Java xử lý HTTP request/response trong ứng dụng web.

Tomcat là gì?
Tomcat là server/container dùng để chạy Servlet/JSP.

Bài này không dùng Tomcat/Servlet vì đây là JavaFX desktop app.
Bài này dùng Socket để JavaFX client giao tiếp trực tiếp với Java server.

============================================================
LUỒNG CHẠY CHƯƠNG TRÌNH
============================================================

1. Chạy database.sql để tạo database karaoke_db.
2. Chạy MainServer.
3. MainServer đọc config.xml.
4. Server mở ServerSocket và chờ client.
5. Chạy Launcher để mở JavaFX Client.
6. Client đọc config.xml để biết serverHost/serverPort.
7. Người dùng đăng nhập hoặc đăng ký.
8. Client gửi Request lên server.
9. Server xử lý trong ClientHandler.
10. Server gọi DAO hoặc Util tương ứng.
11. Server trả Response về client.
12. Client hiển thị kết quả lên giao diện.
*/
public class PROJECT_NOTE {
    // File này chỉ dùng để ghi chú kiến thức khi bảo vệ đồ án.
    // Không cần chạy file này.
}