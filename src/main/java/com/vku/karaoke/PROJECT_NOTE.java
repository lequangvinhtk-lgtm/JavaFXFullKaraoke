package com.vku.karaoke;

/*
===============================================================================
PROJECT NOTE - JAVA FX FULL KARAOKE
===============================================================================

ĐỀ TÀI:
Ứng dụng JavaFX quản lý bài hát Karaoke theo mô hình Client - Server.

MỤC TIÊU CHÍNH:
- Xây dựng ứng dụng quản lý bài hát karaoke.
- Có đăng nhập, đăng ký, phân quyền Admin/User.
- Có quản lý bài hát: thêm, sửa, xóa, tìm kiếm.
- Có quản lý playlist cá nhân.
- Có lưu lịch sử tìm kiếm.
- Có đọc/ghi file TXT bằng IOStream.
- Có xuất dữ liệu XML.
- Có lưu cấu hình bằng config.xml.
- Có kết nối database MySQL bằng JDBC.
- Có Client - Server bằng Socket.
- Có đa luồng để nhiều client truy cập cùng lúc.

CÁC PACKAGE CHÍNH TRONG PROJECT:

1. com.vku.karaoke.model
   -> Chứa các class đối tượng dữ liệu.
   -> Ví dụ: Song, User, Playlist, SearchHistory, Request, Response.

2. com.vku.karaoke.client
   -> Chứa chương trình phía client JavaFX.
   -> Ví dụ: Launcher, MainClientApp.

3. com.vku.karaoke.client.controller
   -> Chứa controller điều khiển giao diện.
   -> Ví dụ: LoginController, KaraokeDashboardController.

4. com.vku.karaoke.client.network
   -> Chứa class kết nối client tới server.
   -> Ví dụ: ClientConnection.

5. com.vku.karaoke.server
   -> Chứa chương trình server chính.
   -> Ví dụ: MainServer.

6. com.vku.karaoke.server.network
   -> Xử lý request từ client.
   -> Ví dụ: ClientHandler.

7. com.vku.karaoke.server.dao
   -> Chứa các class thao tác database.
   -> Ví dụ: SongDAO, UserDAO, PlaylistDAO, SearchHistoryDAO.

8. com.vku.karaoke.utils
   -> Chứa các class tiện ích.
   -> Ví dụ: DBUtil, FileIOUtil, XMLUtil, ConfigXMLUtil, PasswordUtil.

===============================================================================
LUỒNG CHẠY TỔNG THỂ CỦA CHƯƠNG TRÌNH
===============================================================================

1. Chạy file database.sql để tạo database karaoke_db.
2. Database tạo các bảng:
   - users
   - songs
   - playlists
   - playlist_songs
   - search_history

3. Chạy MainServer.
4. MainServer đọc config.xml để lấy:
   - serverHost
   - serverPort
   - dbHost
   - dbPort
   - dbName
   - dbUser
   - dbPassword

5. MainServer mở ServerSocket ở port cấu hình.
6. Server đứng chờ client kết nối.

7. Chạy Launcher để mở JavaFX Client.
8. ClientConnection đọc config.xml để biết serverHost và serverPort.
9. Client tạo Socket kết nối tới server.

10. Người dùng đăng nhập hoặc đăng ký.
11. Client gửi Request lên server.
12. Server nhận Request trong ClientHandler.
13. Server kiểm tra command trong Request.
14. Server gọi DAO hoặc Util tương ứng.
15. Server trả Response về client.
16. Client nhận Response và cập nhật giao diện.

Ví dụ tìm kiếm bài hát:
Người dùng nhập từ khóa
-> Client gửi Request("SEARCH", keyword)
-> Server nhận request trong ClientHandler
-> SearchHistoryDAO lưu lịch sử tìm kiếm
-> SongDAO tìm bài hát trong MySQL
-> Server trả Response chứa danh sách bài hát
-> Client hiển thị kết quả lên TableView.

===============================================================================
1. OOP - OBJECT ORIENTED PROGRAMMING
   LẬP TRÌNH HƯỚNG ĐỐI TƯỢNG
===============================================================================

1.1. OOP LÀ GÌ?

OOP - Object Oriented Programming (Lập trình hướng đối tượng) là cách lập
trình chia chương trình thành các class (lớp) và object (đối tượng).

Một object thường có:
- Field / Attribute (thuộc tính): dữ liệu của đối tượng.
- Method (phương thức): hành động của đối tượng.

Ví dụ:
Một bài hát có:
- id: mã bài hát
- title: tên bài hát
- artist: ca sĩ
- genre: thể loại

Thì ta tạo class Song để đại diện cho bài hát.

1.2. CÂU LỆNH / KỸ THUẬT PHỔ BIẾN

public class Song {
    private String id;
    private String title;

    public Song(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getId() {
        return id;
    }
}

Giải thích:
- class (lớp): khuôn mẫu tạo đối tượng.
- private (riêng tư): che giấu dữ liệu bên trong class.
- constructor (hàm khởi tạo): tạo object ban đầu.
- getter/setter (hàm lấy/gán dữ liệu): truy cập thuộc tính an toàn.
- encapsulation (đóng gói): gom dữ liệu và hành vi vào một class.

1.3. ÁP DỤNG VÀO BÀI Ở CLASS NÀO?

Package:
com.vku.karaoke.model

Các class:
- Song.java
- User.java
- Playlist.java
- SearchHistory.java
- Request.java
- Response.java
- SystemConfig.java

Ý nghĩa từng class:

Song:
- Đại diện cho bài hát.
- Có id, title, artist, genre.
- Dùng khi thêm, sửa, tìm kiếm, hiển thị bài hát.

User:
- Đại diện cho người dùng.
- Có id, username, role.
- Dùng để phân quyền Admin/User.
- Có hàm isAdmin() để kiểm tra quyền.

Playlist:
- Đại diện cho playlist của người dùng.
- Có id, userId, name, createdAt.

SearchHistory:
- Đại diện cho lịch sử tìm kiếm.
- Có id, userId, keyword, searchedAt.

Request:
- Là object client gửi lên server.
- Có command và data.
- command là tên lệnh, ví dụ: LOGIN, SEARCH, ADD.
- data là dữ liệu gửi kèm.

Response:
- Là object server trả về client.
- Có success, message, data.
- success cho biết thành công hay thất bại.
- message là thông báo.
- data là dữ liệu trả về.

SystemConfig:
- Đại diện cho cấu hình hệ thống đọc từ config.xml.
- Có serverHost, serverPort, dbHost, dbPort, dbName, dbUser, dbPassword.

1.4. NÓ CHẠY RA SAO?

Ví dụ chức năng thêm bài hát:

Admin nhập thông tin bài hát
-> LoginController/DashboardController lấy dữ liệu từ giao diện
-> Tạo object Song
-> Tạo Request("ADD", song)
-> ClientConnection gửi Request lên server
-> ClientHandler nhận Request
-> Server kiểm tra quyền Admin
-> SongDAO.addSong(song) lưu vào database
-> Server trả Response thành công
-> Client hiển thị thông báo.

1.5. NÓI KHI BẢO VỆ

"Em áp dụng OOP bằng cách chia dữ liệu thành các class rõ ràng.
Ví dụ Song đại diện cho bài hát, User đại diện cho người dùng,
Playlist đại diện cho danh sách phát. Ngoài ra, em dùng Request và
Response để đóng gói dữ liệu khi client giao tiếp với server. Cách này
giúp code dễ đọc, dễ quản lý và dễ mở rộng."

1.6. CÂU HỎI GIÁO VIÊN CÓ THỂ HỎI

Hỏi: OOP trong bài em nằm ở đâu?
Trả lời:
Dạ, nằm ở các class model như Song, User, Playlist, SearchHistory,
Request và Response. Mỗi class đại diện cho một đối tượng trong hệ thống.

Hỏi: Vì sao phải tạo class Song?
Trả lời:
Dạ, vì một bài hát có nhiều thông tin như mã bài, tên bài, ca sĩ, thể loại.
Đóng gói vào class Song giúp dữ liệu rõ ràng hơn, dễ truyền giữa client
và server, dễ hiển thị và dễ lưu vào database.

Hỏi: Vì sao Request và Response phải implements Serializable?
Trả lời:
Dạ, vì bài em gửi object qua Socket bằng ObjectOutputStream và
ObjectInputStream. Muốn object gửi qua mạng được thì class đó cần
implements Serializable.

===============================================================================
2. MULTITHREADING - ĐA LUỒNG
===============================================================================

2.1. MULTITHREADING LÀ GÌ?

Multithreading (Đa luồng) là kỹ thuật cho phép chương trình xử lý nhiều
công việc cùng lúc.

Trong mô hình Client - Server:
- Nếu không có đa luồng, server chỉ xử lý được một client tại một thời điểm.
- Nếu có đa luồng, mỗi client có thể được xử lý riêng.

Ví dụ:
Client 1 -> Thread 1 (luồng 1)
Client 2 -> Thread 2 (luồng 2)
Client 3 -> Thread 3 (luồng 3)

2.2. CÂU LỆNH / KỸ THUẬT PHỔ BIẾN

Cách cơ bản:

class ClientHandler implements Runnable {
    @Override
    public void run() {
        // Code xử lý client
    }
}

Thread t = new Thread(new ClientHandler());
t.start();

Giải thích:
- Runnable (đối tượng có thể chạy trong luồng).
- run() (hàm chứa code chạy trong luồng).
- Thread (luồng).
- start() (bắt đầu tạo luồng mới).

Trong bài dùng cách tốt hơn:

ExecutorService pool = Executors.newCachedThreadPool();
pool.execute(new ClientHandler(socket));

Giải thích:
- ExecutorService (bộ quản lý luồng).
- Thread pool (nhóm luồng): quản lý nhiều luồng hiệu quả hơn.
- execute() (thực thi tác vụ).
- newCachedThreadPool() tự tạo thêm luồng khi cần.

2.3. ÁP DỤNG VÀO BÀI Ở CLASS NÀO?

Class:
- MainServer.java
- ClientHandler.java

Trong MainServer:
- Tạo ExecutorService.
- Mỗi lần có client kết nối thì server tạo ClientHandler.
- Đưa ClientHandler vào pool.execute(...).

Trong ClientHandler:
- implements Runnable.
- Hàm run() xử lý request của client.

2.4. NÓ CHẠY RA SAO?

Khi chạy server:
1. Server mở ServerSocket.
2. Server chờ client kết nối.
3. Client 1 kết nối vào.
4. Server tạo ClientHandler cho Client 1.
5. ClientHandler chạy trong một luồng riêng.
6. Client 2 kết nối vào.
7. Server tạo ClientHandler khác cho Client 2.
8. Hai client có thể dùng cùng lúc.

 ///// Mở server ra ,
  clinet 1 kết nối vào ,server tạo luồng riêng để xử lí clinet 1
  clinet 2 kết nối vào ,server tạo luồng riêng để xử lí clinet 2
  =>>>>>Hai client có thể dùng cùng lúc.

Nói dễ hiểu:
Mỗi client giống như một khách hàng.
Mỗi ClientHandler giống như một nhân viên phục vụ riêng.
Nhiều khách hàng đến thì có nhiều nhân viên xử lý, server không bị treo.

2.5. NÓI KHI BẢO VỆ

"Em áp dụng đa luồng ở phía server. MainServer dùng ExecutorService để
quản lý luồng. Mỗi khi có client kết nối, server tạo một ClientHandler riêng
và chạy nó trong một luồng riêng. Nhờ vậy nhiều người dùng có thể truy cập
hệ thống cùng lúc."

2.6. CÂU HỎI GIÁO VIÊN CÓ THỂ HỎI

Hỏi: Đa luồng trong bài em nằm ở đâu?
Trả lời:
Dạ, nằm ở MainServer và ClientHandler. MainServer dùng ExecutorService,
còn ClientHandler implements Runnable và xử lý client trong hàm run().

Hỏi: run() khác gì start()?
Trả lời:
Dạ, run() là hàm chứa code cần chạy. start() mới là lệnh tạo luồng mới.
Trong bài em dùng ExecutorService.execute(), thread pool sẽ tự gọi run()
cho từng ClientHandler.

Hỏi: Vì sao cần đa luồng?
Trả lời:
Dạ, vì server có thể có nhiều client cùng kết nối. Nếu không dùng đa luồng
thì client sau phải chờ client trước xử lý xong. Đa luồng giúp server xử lý
nhiều client cùng lúc.

===============================================================================
3. IOSTREAM - ĐỌC / GHI FILE
===============================================================================

3.1. IOSTREAM LÀ GÌ?

IOStream - Input/Output Stream (Luồng vào/ra) là kỹ thuật dùng để đọc và
ghi dữ liệu.

Trong bài này, IOStream dùng để:
- Export danh sách bài hát ra file TXT.
- Import danh sách bài hát từ file TXT vào database.

3.2. CÂU LỆNH / KỸ THUẬT PHỔ BIẾN

Ghi file:

BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
writer.write("data");
writer.newLine();

Đọc file:

BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
String line = reader.readLine();

Dùng try-with-resources:

try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
    writer.write("Hello");
}

Giải thích:
- BufferedWriter (bộ ghi file).
- BufferedReader (bộ đọc file).
- write() (ghi dữ liệu).
- readLine() (đọc từng dòng).
- newLine() (xuống dòng).
- try-with-resources (tự đóng file sau khi dùng).
- UTF_8 (mã hóa hỗ trợ tiếng Việt tốt hơn).

3.3. ÁP DỤNG VÀO BÀI Ở CLASS NÀO?

Class:
- FileIOUtil.java
- ClientHandler.java
- KaraokeDashboardController.java

FileIOUtil có:
- saveSongsToTxt(): ghi danh sách bài hát ra TXT.
- loadSongsFromTxt(): đọc danh sách bài hát từ TXT.

ClientHandler có command:
- EXPORT_TXT: xuất file TXT.
- IMPORT_TXT: nhập file TXT.

KaraokeDashboardController có nút:
- Xuất TXT.
- Nhập TXT.

3.4. NÓ CHẠY RA SAO?

Khi bấm Xuất TXT:
Client gửi Request("EXPORT_TXT")
-> Server lấy danh sách bài hát từ MySQL
-> FileIOUtil.saveSongsToTxt() ghi ra backup_songs.txt
-> Server trả Response báo thành công.

Khi bấm Nhập TXT:
Client gửi Request("IMPORT_TXT")
-> Server đọc backup_songs.txt
-> Mỗi dòng tách theo dấu |
-> Tạo object Song
-> songDAO.upsertSong(song) lưu vào database
-> Server trả Response báo thành công.

Định dạng file TXT:
id|title|artist|genre

Ví dụ:
S001|Nơi này có anh|Sơn Tùng M-TP|Pop

3.5. NÓI KHI BẢO VỆ

"Em áp dụng IOStream để đọc và ghi dữ liệu bài hát ra file TXT. Khi xuất TXT,
server lấy danh sách bài hát từ database và ghi ra backup_songs.txt. Khi nhập
TXT, server đọc file này, tạo object Song rồi lưu lại vào database."

3.6. CÂU HỎI GIÁO VIÊN CÓ THỂ HỎI

Hỏi: IOStream trong bài dùng để làm gì?
Trả lời:
Dạ, dùng để import và export danh sách bài hát bằng file TXT.

Hỏi: Đọc file dùng class nào?
Trả lời:
Dạ, dùng BufferedReader.

Hỏi: Ghi file dùng class nào?
Trả lời:
Dạ, dùng BufferedWriter.

Hỏi: Vì sao dùng UTF-8?
Trả lời:
Dạ, để đọc và ghi tiếng Việt không bị lỗi font.

===============================================================================
4. XML - EXTENSIBLE MARKUP LANGUAGE
   NGÔN NGỮ ĐÁNH DẤU MỞ RỘNG
===============================================================================

4.1. XML LÀ GÌ?

XML - Extensible Markup Language (Ngôn ngữ đánh dấu mở rộng) là dạng file
lưu dữ liệu bằng các thẻ.

Ví dụ:

<song id="S001">
    <title>Nơi này có anh</title>
    <artist>Sơn Tùng M-TP</artist>
    <genre>Pop</genre>
</song>

XML dễ đọc, có cấu trúc rõ ràng, phù hợp để lưu cấu hình hoặc trao đổi dữ liệu.

4.2. CÂU LỆNH / KỸ THUẬT PHỔ BIẾN

Đọc XML:

Document doc = DocumentBuilderFactory.newInstance()
        .newDocumentBuilder()
        .parse(file);

doc.getDocumentElement().normalize();

Tạo XML:

Document doc = DocumentBuilderFactory.newInstance()
        .newDocumentBuilder()
        .newDocument();

Element root = doc.createElement("config");
doc.appendChild(root);

Ghi XML:

Transformer transformer = TransformerFactory.newInstance().newTransformer();
transformer.transform(new DOMSource(doc), new StreamResult(new File("config.xml")));

Giải thích:
- DOM (mô hình cây XML).
- Document (tài liệu XML).
- Element (thẻ XML).
- parse() (đọc XML).
- Transformer (ghi XML ra file).
- normalize() (chuẩn hóa cấu trúc XML).

4.3. ÁP DỤNG VÀO BÀI Ở CLASS NÀO?

Class:
- ConfigXMLUtil.java
- XMLUtil.java
- DBUtil.java
- ClientConnection.java

Bài dùng XML cho 2 việc:

1. config.xml:
   - Lưu cấu hình hệ thống.
   - serverHost
   - serverPort
   - dbHost
   - dbPort
   - dbName
   - dbUser
   - dbPassword

2. backup_songs.xml:
   - Xuất danh sách bài hát ra XML.
   - Dùng để sao lưu hoặc chia sẻ dữ liệu.

ConfigXMLUtil:
- Đọc và ghi config.xml.

DBUtil:
- Dùng thông tin database trong config.xml để kết nối MySQL.

ClientConnection:
- Dùng serverHost và serverPort trong config.xml để kết nối server.

XMLUtil:
- Xuất danh sách bài hát ra file XML.

4.4. NÓ CHẠY RA SAO?

Khi chạy chương trình:
ConfigXMLUtil đọc config.xml
-> DBUtil lấy thông tin database
-> Tạo kết nối MySQL
-> ClientConnection lấy serverHost/serverPort
-> Client kết nối tới server.

Khi bấm Xuất XML:
Client gửi Request("EXPORT_XML")
-> Server lấy danh sách bài hát từ database
-> XMLUtil tạo file backup_songs.xml
-> Mỗi bài hát được ghi thành một thẻ song.

4.5. NÓI KHI BẢO VỆ

"Em dùng XML cho hai mục đích. Thứ nhất, config.xml dùng để lưu cấu hình
server và database, giúp đổi cấu hình mà không cần sửa code. Thứ hai,
XMLUtil dùng để xuất danh sách bài hát ra backup_songs.xml để sao lưu
hoặc chia sẻ dữ liệu."

4.6. CÂU HỎI GIÁO VIÊN CÓ THỂ HỎI

Hỏi: XML trong bài em dùng để làm gì?
Trả lời:
Dạ, dùng để lưu cấu hình trong config.xml và xuất danh sách bài hát ra XML.

Hỏi: Vì sao không hard-code thông tin database trong Java?
Trả lời:
Dạ, vì nếu đổi mật khẩu hoặc tên database thì phải sửa code. Dùng config.xml
giúp đổi cấu hình dễ hơn.

===============================================================================
5. DATABASE / JDBC
===============================================================================

5.1. DATABASE VÀ JDBC LÀ GÌ?

Database (Cơ sở dữ liệu) là nơi lưu dữ liệu lâu dài.

JDBC - Java Database Connectivity (Kết nối Java với cơ sở dữ liệu) là công
nghệ giúp Java kết nối và thao tác với database như MySQL.

5.2. CÂU LỆNH / KỸ THUẬT PHỔ BIẾN

Connection connection = DriverManager.getConnection(url, user, password);

PreparedStatement ps = connection.prepareStatement(sql);
ps.setString(1, keyword);

ResultSet rs = ps.executeQuery();

while (rs.next()) {
    String title = rs.getString("title");
}

ps.executeUpdate();

Giải thích:
- Connection (kết nối database).
- DriverManager (trình quản lý kết nối).
- PreparedStatement (câu SQL có tham số).
- ResultSet (kết quả của SELECT).
- executeQuery() dùng cho SELECT.
- executeUpdate() dùng cho INSERT, UPDATE, DELETE.

SQL phổ biến:
SELECT * FROM songs;
INSERT INTO songs(id, title, artist, genre) VALUES (?, ?, ?, ?);
UPDATE songs SET title = ? WHERE id = ?;
DELETE FROM songs WHERE id = ?;

5.3. ÁP DỤNG VÀO BÀI Ở CLASS NÀO?

File:
- database.sql

Class:
- DBUtil.java
- SongDAO.java
- UserDAO.java
- PlaylistDAO.java
- SearchHistoryDAO.java

Database có các bảng:
- users: lưu tài khoản người dùng.
- songs: lưu bài hát.
- playlists: lưu playlist của user.
- playlist_songs: lưu bài hát thuộc playlist nào.
- search_history: lưu lịch sử tìm kiếm.

DBUtil:
- Tạo kết nối MySQL.

SongDAO:
- getAllSongs()
- searchSongs()
- addSong()
- updateSong()
- deleteSong()
- upsertSong()

UserDAO:
- authenticate()
- usernameExists()
- registerUser()

PlaylistDAO:
- findByUser()
- createPlaylist()
- deletePlaylist()
- addSongToPlaylist()
- removeSongFromPlaylist()
- getSongsInPlaylist()

SearchHistoryDAO:
- saveKeyword()
- findByUser()
- clearByUser()

5.4. NÓ CHẠY RA SAO?

Ví dụ tìm kiếm bài hát:
Người dùng nhập từ khóa
-> Client gửi Request("SEARCH", keyword)
-> Server nhận request
-> SearchHistoryDAO.saveKeyword() lưu lịch sử
-> SongDAO.searchSongs() tìm bài hát trong bảng songs
-> Database trả kết quả
-> Server đóng gói vào Response
-> Client hiển thị lên TableView.

Ví dụ thêm bài hát:
Admin nhập thông tin bài hát
-> Client tạo object Song
-> Gửi Request("ADD", song)
-> Server kiểm tra quyền Admin
-> SongDAO.addSong(song)
-> JDBC chạy INSERT INTO songs
-> Database lưu bài hát
-> Server trả Response thành công.

5.5. NÓI KHI BẢO VỆ

"Em dùng MySQL để lưu dữ liệu lâu dài và dùng JDBC để Java kết nối với
MySQL. Em tách thao tác database vào các lớp DAO như SongDAO, UserDAO,
PlaylistDAO, SearchHistoryDAO. Cách tách DAO giúp code rõ ràng, dễ bảo trì
và đúng trách nhiệm từng class."

5.6. CÂU HỎI GIÁO VIÊN CÓ THỂ HỎI

Hỏi: JDBC trong bài nằm ở đâu?
Trả lời:
Dạ, nằm ở DBUtil và các class DAO.

Hỏi: DAO là gì?
Trả lời:
Dạ, DAO là Data Access Object, tức là lớp chuyên truy cập và thao tác dữ liệu
trong database.

Hỏi: Vì sao dùng PreparedStatement?
Trả lời:
Dạ, PreparedStatement giúp truyền tham số vào SQL rõ ràng hơn, hạn chế lỗi
khi ghép chuỗi SQL thủ công.

Hỏi: Playlist lưu như thế nào?
Trả lời:
Dạ, bảng playlists lưu thông tin playlist, còn bảng playlist_songs lưu bài hát
nào thuộc playlist nào. Playlist gắn với user_id nên mỗi user chỉ quản lý
playlist của mình.

===============================================================================
6. JAVA SECURITY - BẢO MẬT JAVA
===============================================================================

6.1. SECURITY TRONG BÀI LÀ GÌ?
 Là các giải pháp bảo vệ ứng dụng,
 dữ liệu và mã nguồn khỏi các lỗ hổng hoặc truy cập trái phép.

Trong bài này, Java Security tập trung vào:
- Login (đăng nhập).
- Register (đăng ký).
- Password hashing (băm mật khẩu).
- Role (vai trò): ADMIN / USER.
- Authorization (phân quyền).
- Kiểm tra quyền ở server.

6.2. CÂU LỆNH / KỸ THUẬT PHỔ BIẾN

Hash mật khẩu SHA-256:

MessageDigest digest = MessageDigest.getInstance("SHA-256");
byte[] encoded = digest.digest(password.getBytes(StandardCharsets.UTF_8));

Kiểm tra quyền:

if (!currentUser.isAdmin()) {
    throw new SecurityException("Bạn không có quyền ADMIN");
}

Giải thích:
- MessageDigest (lớp dùng để băm dữ liệu).
- SHA-256 (thuật toán băm 256-bit).
- Hash (băm): biến mật khẩu thành chuỗi khó đọc ngược.
- Role (vai trò): quyền của user.
- Authorization (phân quyền): kiểm tra user có được phép làm chức năng đó không.

6.3. ÁP DỤNG VÀO BÀI Ở CLASS NÀO?

Class:
- PasswordUtil.java
- UserDAO.java
- User.java
- ClientHandler.java
- LoginController.java
- KaraokeDashboardController.java

PasswordUtil:
- Hash mật khẩu bằng SHA-256.

UserDAO:
- authenticate(): đăng nhập.
- registerUser(): đăng ký tài khoản.
- usernameExists(): kiểm tra trùng username.

User:
- Có role.
- Có isAdmin().

ClientHandler:
- Lưu currentUser sau khi đăng nhập.
- Nếu chưa đăng nhập thì không cho dùng chức năng.
- Các lệnh ADD, UPDATE, DELETE, IMPORT_TXT gọi requireAdmin().

LoginController:
- Gửi Request LOGIN hoặc REGISTER.

KaraokeDashboardController:
- Nếu không phải Admin thì disable nút thêm/sửa/xóa/import trên giao diện.

6.4. NÓ CHẠY RA SAO?

Đăng ký:
Người dùng bấm Đăng ký
-> Nhập username, password, confirm password
-> Client kiểm tra dữ liệu nhập
-> Client gửi Request("REGISTER", username/password)
-> Server kiểm tra username đã tồn tại chưa
-> Server hash password
-> UserDAO lưu tài khoản mới vào bảng users với role USER
-> Server trả Response báo thành công.

Đăng nhập:
Người dùng nhập username/password
-> Client gửi Request("LOGIN", username/password)
-> Server hash password người dùng nhập
-> So sánh với password_hash trong database
-> Nếu đúng, server tạo currentUser
-> Server trả Response chứa User
-> Client mở Dashboard.

Phân quyền:
User thường cố thêm bài hát
-> Client có thể đã bị khóa nút
-> Nếu cố gửi Request("ADD") lên server
-> ClientHandler gọi requireAdmin()
-> Nếu currentUser không phải ADMIN thì server từ chối.

6.5. NÓI KHI BẢO VỆ

"Em áp dụng Security bằng đăng nhập, đăng ký và phân quyền Admin/User.
Mật khẩu không lưu trực tiếp mà được hash bằng SHA-256. Admin có quyền
thêm, sửa, xóa bài hát và import TXT. User thường chỉ xem, tìm kiếm, quản lý
playlist và lịch sử. Việc phân quyền được kiểm tra ở server bằng requireAdmin,
nên không chỉ phụ thuộc vào giao diện."

6.6. CÂU HỎI GIÁO VIÊN CÓ THỂ HỎI

Hỏi: Mật khẩu có lưu trực tiếp không?
Trả lời:
Dạ không. Em lưu password_hash. Khi đăng nhập thì mật khẩu người dùng nhập
được hash rồi so sánh với password_hash trong database.

Hỏi: Admin khác User như thế nào?
Trả lời:
Dạ, Admin có quyền thêm, sửa, xóa bài hát và import TXT. User thường chỉ
được xem, tìm kiếm, tạo playlist và xem lịch sử tìm kiếm.

Hỏi: Phân quyền kiểm tra ở đâu?
Trả lời:
Dạ, có kiểm tra ở giao diện và ở server. Quan trọng nhất là server kiểm tra
bằng requireAdmin() trong ClientHandler.

Hỏi: Tài khoản đăng ký mới có quyền gì?
Trả lời:
Dạ, tài khoản đăng ký mới mặc định là USER để tránh người dùng tự tạo Admin.

===============================================================================
7. NETWORKING - LẬP TRÌNH MẠNG
===============================================================================

7.1. NETWORKING LÀ GÌ?

Networking (Lập trình mạng) là kỹ thuật giúp các chương trình giao tiếp với
nhau qua mạng.

Trong Java có nhiều cách:
- Socket (ổ cắm kết nối mạng).
- Servlet (class xử lý request web).
- Tomcat (server/container chạy Servlet).
- REST API (API HTTP).
- WebSocket (kết nối web thời gian thực).

7.2. BÀI NÀY DÙNG GÌ?

Bài này dùng:
Socket Client - Server.

Bài này không dùng:
Servlet / Tomcat.

Lý do:
Đây là ứng dụng JavaFX desktop. Client là chương trình JavaFX, nên em dùng
Socket để client giao tiếp trực tiếp với server Java.

7.3. SOCKET LÀ GÌ?

Socket là điểm kết nối giữa client và server.

Mô hình:
JavaFX Client
-> Socket
-> ServerSocket
-> ClientHandler
-> Database/Util
-> Response trả về Client.

7.4. CÂU LỆNH / KỸ THUẬT PHỔ BIẾN

Phía server:

ServerSocket serverSocket = new ServerSocket(9999);
Socket socket = serverSocket.accept();

Phía client:

Socket socket = new Socket("localhost", 9999);

Gửi và nhận object:

ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

out.writeObject(request);
Response response = (Response) in.readObject();

Giải thích:
- ServerSocket (socket phía server).
- accept() (chờ và nhận client kết nối).
- Socket (kết nối giữa client và server).
- ObjectOutputStream (luồng gửi object).
- ObjectInputStream (luồng nhận object).
- writeObject() (gửi object).
- readObject() (nhận object).

7.5. ÁP DỤNG VÀO BÀI Ở CLASS NÀO?

Class:
- MainServer.java
- ClientConnection.java
- ClientHandler.java
- Request.java
- Response.java

MainServer:
- Mở ServerSocket.
- Chờ client kết nối.

ClientConnection:
- Tạo Socket kết nối tới server.
- Gửi Request.
- Nhận Response.

ClientHandler:
- Nhận Request từ client.
- Xử lý command.
- Trả Response.

Request:
- Gói yêu cầu từ client.

Response:
- Gói kết quả từ server.

7.6. NÓ CHẠY RA SAO?

Ví dụ tìm kiếm bài hát:
Người dùng nhập từ khóa
-> Client tạo Request("SEARCH", keyword)
-> ObjectOutputStream gửi Request qua Socket
-> Server nhận trong ClientHandler
-> Server gọi SearchHistoryDAO và SongDAO
-> Server tạo Response chứa danh sách bài hát
-> ObjectOutputStream gửi Response về client
-> Client nhận Response bằng ObjectInputStream
-> Client hiển thị kết quả.

7.7. SERVLET LÀ GÌ?

Servlet là class Java chạy trên web server để xử lý HTTP request và HTTP response.

Nếu bài này làm dạng web thì có thể có các đường dẫn:
- /login
- /searchSong
- /addSong
- /deleteSong
- /playlist

Khi đó browser hoặc web client gửi HTTP request đến Servlet.
Servlet xử lý và trả HTML hoặc JSON.

7.8. TOMCAT LÀ GÌ?

Tomcat là server/container dùng để chạy Servlet và JSP.

Nói dễ hiểu:
- Servlet là code Java xử lý request web.
- Tomcat là môi trường để chạy Servlet.

7.9. BÀI NÀY CÓ DÙNG SERVLET/TOMCAT KHÔNG?

Không.

Câu trả lời chuẩn:
"Bài em không dùng Servlet/Tomcat vì đây là ứng dụng JavaFX desktop.
Em dùng Socket để JavaFX client giao tiếp trực tiếp với Java server.
Nếu chuyển sang web app thì em có thể dùng Servlet chạy trên Tomcat."

7.10. NÓI KHI BẢO VỆ

"Em áp dụng Networking bằng Socket. Server dùng MainServer để mở ServerSocket.
Client dùng ClientConnection để tạo Socket kết nối tới server. Client gửi
Request qua ObjectOutputStream, server nhận và xử lý trong ClientHandler,
sau đó trả Response về client. Bài em không dùng Servlet/Tomcat vì đây là
ứng dụng JavaFX desktop, không phải web app."

7.11. CÂU HỎI GIÁO VIÊN CÓ THỂ HỎI

Hỏi: Bài em dùng Socket hay Servlet?
Trả lời:
Dạ, bài em dùng Socket.

Hỏi: Tomcat là gì?
Trả lời:
Dạ, Tomcat là server/container dùng để chạy Servlet/JSP trong ứng dụng web Java.

Hỏi: Servlet là gì?
Trả lời:
Dạ, Servlet là class Java chạy trên web server để xử lý HTTP request và trả
HTTP response.

Hỏi: Vì sao em không dùng Tomcat/Servlet?
Trả lời:
Dạ, vì bài em là JavaFX desktop app. Em chọn Socket để client JavaFX giao tiếp
trực tiếp với server Java. Servlet/Tomcat phù hợp hơn với web app.

===============================================================================
8. REQUEST / RESPONSE - CÁCH CLIENT VÀ SERVER TRAO ĐỔI DỮ LIỆU
===============================================================================

8.1. REQUEST LÀ GÌ?

Request là object chứa yêu cầu client gửi lên server.

Request gồm:
- command: tên chức năng.
- data: dữ liệu gửi kèm.

Ví dụ:
Request("LOGIN", new String[]{username, password})
Request("SEARCH", keyword)
Request("ADD", song)

8.2. RESPONSE LÀ GÌ?

Response là object server trả về client.

Response gồm:
- success: true/false.
- message: thông báo.
- data: dữ liệu trả về.

Ví dụ:
Response.ok("Đăng nhập thành công", user)
Response.fail("Sai tài khoản hoặc mật khẩu")
Response.ok("Tìm kiếm thành công", listSongs)

8.3. CÁC COMMAND QUAN TRỌNG TRONG BÀI

LOGIN:
- Đăng nhập.

REGISTER:
- Đăng ký tài khoản mới.

GET_ALL:
- Lấy toàn bộ bài hát.

SEARCH:
- Tìm kiếm bài hát.
- Đồng thời lưu lịch sử tìm kiếm.

ADD:
- Thêm bài hát.
- Chỉ Admin.

UPDATE:
- Sửa bài hát.
- Chỉ Admin.

DELETE:
- Xóa bài hát.
- Chỉ Admin.

EXPORT_TXT:
- Xuất danh sách bài hát ra backup_songs.txt.

IMPORT_TXT:
- Nhập danh sách bài hát từ backup_songs.txt.
- Chỉ Admin.

EXPORT_XML:
- Xuất danh sách bài hát ra backup_songs.xml.

GET_HISTORY:
- Lấy lịch sử tìm kiếm của user.

CLEAR_HISTORY:
- Xóa lịch sử tìm kiếm.

PLAYLISTS:
- Lấy danh sách playlist của user.

PLAYLIST_CREATE:
- Tạo playlist.

PLAYLIST_DELETE:
- Xóa playlist.

PLAYLIST_ADD_SONG:
- Thêm bài hát vào playlist.

PLAYLIST_REMOVE_SONG:
- Xóa bài hát khỏi playlist.

PLAYLIST_SONGS:
- Lấy bài hát trong playlist.

8.4. NÓI KHI BẢO VỆ

"Em dùng Request và Response để chuẩn hóa giao tiếp giữa client và server.
Client không gọi trực tiếp database mà gửi Request lên server. Server xử lý
xong thì trả Response về client. Nhờ vậy mô hình Client - Server rõ ràng hơn."

===============================================================================
9. SERIALIZABLE - GỬI OBJECT QUA SOCKET
===============================================================================

9.1. SERIALIZABLE LÀ GÌ?

Serializable là interface trong Java dùng để đánh dấu một object có thể được
chuyển thành dạng byte để lưu file hoặc gửi qua mạng.

Trong bài này, các object như Song, User, Request, Response cần implements
Serializable vì chúng được gửi qua Socket bằng ObjectOutputStream.

9.2. CÂU LỆNH PHỔ BIẾN

public class Song implements Serializable {
    private static final long serialVersionUID = 1L;
}

Giải thích:
- implements Serializable: cho phép object được gửi qua ObjectOutputStream.
- serialVersionUID: mã phiên bản của class khi serialize.

9.3. ÁP DỤNG VÀO BÀI

Các class nên implements Serializable:
- Song
- User
- Playlist
- SearchHistory
- Request
- Response

9.4. NÓI KHI BẢO VỆ

"Vì bài em gửi object qua Socket bằng ObjectOutputStream nên các model như
Song, User, Request, Response cần implements Serializable. Nếu không có
Serializable thì Java không thể chuyển object thành dữ liệu để gửi qua mạng."

===============================================================================
10. JAVA FX - GIAO DIỆN
===============================================================================

10.1. JAVAFX LÀ GÌ?

JavaFX là thư viện Java dùng để xây dựng giao diện ứng dụng desktop.

Trong bài này:
- login.fxml: giao diện đăng nhập/đăng ký.
- dashboard.fxml: giao diện chính.
- style.css: định dạng giao diện.
- LoginController: xử lý màn hình đăng nhập/đăng ký.
- KaraokeDashboardController: xử lý màn hình chính.

10.2. ÁP DỤNG VÀO BÀI

LoginController:
- Lấy username/password từ giao diện.
- Gửi Request LOGIN hoặc REGISTER lên server.
- Nếu đăng nhập đúng thì mở Dashboard.

KaraokeDashboardController:
- Hiển thị danh sách bài hát bằng TableView.
- Hiển thị playlist bằng ListView.
- Hiển thị lịch sử tìm kiếm bằng TableView.
- Gửi request khi người dùng bấm các nút.

10.3. ĐIỂM QUAN TRỌNG

Controller không kết nối database trực tiếp.
Controller chỉ gửi Request lên server.
Server mới gọi DAO để thao tác database.

10.4. NÓI KHI BẢO VỆ

"JavaFX trong bài chỉ là phần giao diện. Các controller không thao tác
database trực tiếp mà gửi request lên server. Điều này giúp bài giữ đúng
mô hình Client - Server."

===============================================================================
11. PHÂN QUYỀN ADMIN / USER
===============================================================================

11.1. ADMIN LÀM ĐƯỢC GÌ?

Admin có quyền:
- Xem bài hát.
- Tìm kiếm bài hát.
- Thêm bài hát.
- Sửa bài hát.
- Xóa bài hát.
- Import TXT.
- Export TXT.
- Export XML.
- Quản lý playlist.
- Xem lịch sử tìm kiếm.

11.2. USER LÀM ĐƯỢC GÌ?

User có quyền:
- Xem bài hát.
- Tìm kiếm bài hát.
- Tạo playlist.
- Thêm bài hát vào playlist cá nhân.
- Xóa bài khỏi playlist cá nhân.
- Xem lịch sử tìm kiếm.
- Xóa lịch sử tìm kiếm.

User không được:
- Thêm bài hát vào kho chung.
- Sửa bài hát.
- Xóa bài hát.
- Import TXT.

11.3. KIỂM TRA QUYỀN Ở ĐÂU?

1. Giao diện:
KaraokeDashboardController disable nút thêm/sửa/xóa/import nếu không phải Admin.

2. Server:
ClientHandler dùng requireAdmin() để kiểm tra thật sự.

Điểm quan trọng:
Kiểm tra ở server quan trọng hơn giao diện.
Vì nếu ai đó tự gửi request trái phép, server vẫn chặn lại.

11.4. NÓI KHI BẢO VỆ

"Em phân quyền ở cả giao diện và server. Giao diện khóa các nút của User,
nhưng server vẫn kiểm tra bằng requireAdmin(). Vì vậy bảo mật không chỉ phụ
thuộc vào giao diện."

===============================================================================
12. CÁC CHỨC NĂNG CHÍNH VÀ LUỒNG CHẠY
===============================================================================

12.1. ĐĂNG KÝ TÀI KHOẢN

Luồng chạy:
Người dùng chọn đăng ký
-> Nhập username, password, confirm password
-> LoginController kiểm tra dữ liệu
-> Client gửi Request("REGISTER")
-> ClientHandler gọi handleRegister()
-> UserDAO kiểm tra username trùng
-> PasswordUtil hash mật khẩu
-> UserDAO lưu user mới với role USER
-> Server trả Response
-> Client hiện thông báo thành công.

12.2. ĐĂNG NHẬP

Luồng chạy:
Người dùng nhập username/password
-> Client gửi Request("LOGIN")
-> ClientHandler gọi handleLogin()
-> UserDAO hash password và kiểm tra database
-> Đúng thì server lưu currentUser
-> Server trả Response chứa User
-> Client mở Dashboard.

12.3. TÌM KIẾM BÀI HÁT

Luồng chạy:
Người dùng nhập keyword
-> Client gửi Request("SEARCH", keyword)
-> Server lưu keyword vào search_history
-> Server gọi SongDAO.searchSongs()
-> Database trả danh sách bài hát
-> Server trả Response
-> Client hiển thị lên TableView.

12.4. THÊM BÀI HÁT

Luồng chạy:
Admin nhập thông tin bài hát
-> Client tạo Song
-> Client gửi Request("ADD", song)
-> Server gọi requireAdmin()
-> SongDAO.addSong()
-> Database lưu bài hát
-> Server trả Response.

12.5. SỬA BÀI HÁT

Luồng chạy:
Admin chọn bài hát
-> Sửa thông tin
-> Client gửi Request("UPDATE", song)
-> Server kiểm tra Admin
-> SongDAO.updateSong()
-> Database cập nhật bài hát
-> Server trả Response.

12.6. XÓA BÀI HÁT

Luồng chạy:
Admin chọn bài hát
-> Client gửi Request("DELETE", songId)
-> Server kiểm tra Admin
-> SongDAO.deleteSong()
-> Database xóa bài hát
-> Server trả Response.

12.7. TẠO PLAYLIST

Luồng chạy:
User nhập tên playlist
-> Client gửi Request("PLAYLIST_CREATE", name)
-> Server lấy currentUser.id
-> PlaylistDAO.createPlaylist(userId, name)
-> Database lưu playlist
-> Server trả Response.

12.8. THÊM BÀI HÁT VÀO PLAYLIST

Luồng chạy:
User chọn playlist và bài hát
-> Client gửi Request("PLAYLIST_ADD_SONG", playlistId|songId)
-> Server kiểm tra playlist thuộc user hiện tại
-> PlaylistDAO.addSongToPlaylist()
-> Database lưu vào playlist_songs.

12.9. LỊCH SỬ TÌM KIẾM

Luồng chạy:
Mỗi lần user tìm kiếm
-> SearchHistoryDAO.saveKeyword()
-> Từ khóa lưu vào bảng search_history theo user_id.
Khi xem lịch sử
-> Client gửi GET_HISTORY
-> Server trả lịch sử của currentUser.

12.10. EXPORT TXT

Luồng chạy:
Client gửi EXPORT_TXT
-> Server lấy toàn bộ bài hát
-> FileIOUtil.saveSongsToTxt()
-> Tạo backup_songs.txt.

12.11. IMPORT TXT

Luồng chạy:
Client gửi IMPORT_TXT
-> Server kiểm tra Admin
-> FileIOUtil.loadSongsFromTxt()
-> Đọc từng dòng TXT
-> Tạo Song
-> songDAO.upsertSong()
-> Lưu vào database.

12.12. EXPORT XML

Luồng chạy:
Client gửi EXPORT_XML
-> Server lấy toàn bộ bài hát
-> XMLUtil.exportSongsToXML()
-> Tạo backup_songs.xml.

===============================================================================
13. CÂU NÓI BẢO VỆ NGẮN GỌN
===============================================================================

Đề tài của em là ứng dụng JavaFX quản lý bài hát Karaoke theo mô hình
Client - Server.

Về OOP, em tạo các class model như Song, User, Playlist, SearchHistory,
Request và Response để đóng gói dữ liệu.

Về JDBC, em dùng MySQL để lưu users, songs, playlists, playlist_songs và
search_history. Em tách các thao tác database vào các class DAO như SongDAO,
UserDAO, PlaylistDAO và SearchHistoryDAO.

Về Networking, em dùng Socket. Server mở ServerSocket trong MainServer,
client kết nối bằng ClientConnection. Client gửi Request, server xử lý trong
ClientHandler rồi trả Response.

Về Multithreading, MainServer dùng ExecutorService. Mỗi client được xử lý
bằng một ClientHandler chạy trên một luồng riêng.

Về IOStream, em dùng FileIOUtil để đọc/ghi danh sách bài hát ra file TXT.

Về XML, em dùng ConfigXMLUtil để đọc config.xml và XMLUtil để export danh
sách bài hát ra XML.

Về Security, em có đăng nhập, đăng ký, hash mật khẩu SHA-256 và phân quyền
Admin/User. Các chức năng quan trọng được kiểm tra ở server bằng requireAdmin.

Bài của em không dùng Tomcat/Servlet vì đây là JavaFX desktop app. Em dùng
Socket để client JavaFX giao tiếp trực tiếp với server Java.

===============================================================================
14. CÂU HỎI GIÁO VIÊN CÓ THỂ HỎI
===============================================================================

1. OOP trong bài em nằm ở đâu?
-> Nằm ở các class model như Song, User, Playlist, SearchHistory, Request,
Response.

2. Vì sao dùng Request và Response?
-> Để chuẩn hóa dữ liệu giao tiếp giữa client và server.

3. Vì sao các model implements Serializable?
-> Vì object được gửi qua Socket bằng ObjectOutputStream.

4. Đa luồng trong bài nằm ở đâu?
-> MainServer dùng ExecutorService, ClientHandler implements Runnable.

5. run() dùng để làm gì?
-> run() chứa code xử lý client trong luồng riêng.

6. IOStream dùng để làm gì?
-> Dùng để import/export bài hát bằng file TXT.

7. XML dùng để làm gì?
-> Dùng để lưu config.xml và export backup_songs.xml.

8. JDBC nằm ở đâu?
-> DBUtil và các class DAO.

9. DAO là gì?
-> Là class chuyên thao tác dữ liệu với database.

10. PreparedStatement dùng để làm gì?
-> Dùng để truyền tham số vào SQL rõ ràng và an toàn hơn ghép chuỗi.

11. Mật khẩu có lưu trực tiếp không?
-> Không, mật khẩu được hash bằng SHA-256.

12. Admin và User khác nhau thế nào?
-> Admin được thêm/sửa/xóa/import. User chỉ xem, tìm kiếm, quản lý playlist
và lịch sử.

13. Phân quyền kiểm tra ở đâu?
-> Kiểm tra ở giao diện và server. Quan trọng nhất là requireAdmin() ở server.

14. Bài dùng Socket hay Servlet?
-> Bài dùng Socket.

15. Tomcat là gì?
-> Tomcat là server/container dùng để chạy Servlet/JSP.

16. Servlet là gì?
-> Servlet là class Java xử lý HTTP request/response trong ứng dụng web.

17. Vì sao bài không dùng Tomcat/Servlet?
-> Vì bài là JavaFX desktop app nên dùng Socket phù hợp hơn.

18. Client có kết nối database trực tiếp không?
-> Không. Client chỉ gửi Request lên server. Server mới gọi DAO kết nối database.

19. Khi tìm kiếm bài hát thì hệ thống chạy ra sao?
-> Client gửi SEARCH, server lưu lịch sử, gọi SongDAO tìm database, trả kết quả
về client.

20. Khi đăng ký tài khoản thì quyền mặc định là gì?
-> Quyền USER, để tránh người dùng tự tạo tài khoản Admin.

===============================================================================
FILE NÀY CHỈ LÀ GHI CHÚ ÔN BẢO VỆ
===============================================================================

File PROJECT_NOTE.java không cần chạy.
File này dùng để mở ra xem nhanh khi giáo viên hỏi về kỹ thuật trong đồ án.
*/

public class PROJECT_NOTE {
    // Không cần viết code chạy trong class này.
}