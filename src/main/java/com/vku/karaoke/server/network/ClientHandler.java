package com.vku.karaoke.server.network;

import com.vku.karaoke.model.Request;
import com.vku.karaoke.model.Response;
import com.vku.karaoke.model.Song;
import com.vku.karaoke.model.User;
import com.vku.karaoke.server.dao.PlaylistDAO;
import com.vku.karaoke.server.dao.SearchHistoryDAO;
import com.vku.karaoke.server.dao.SongDAO;
import com.vku.karaoke.server.dao.UserDAO;
import com.vku.karaoke.utils.FileIOUtil;
import com.vku.karaoke.utils.XMLUtil;

import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;


public class ClientHandler implements Runnable {
    private final Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private final UserDAO userDAO = new UserDAO();
    private final SongDAO songDAO = new SongDAO();
    private final PlaylistDAO playlistDAO = new PlaylistDAO();
    private final SearchHistoryDAO historyDAO = new SearchHistoryDAO();

    private User currentUser;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    /*
Hàm run() là hàm chạy trong luồng riêng của từng client.

Trong hàm này:
1. Tạo ObjectOutputStream để gửi dữ liệu về client.
2. Tạo ObjectInputStream để nhận dữ liệu từ client.
3. Chạy vòng lặp while(true) để liên tục nhận Request.
4. Mỗi Request được xử lý bằng handleRequest().
5. Sau khi xử lý, server gửi Response về client.
*/
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());

            while (true) {
                Object object = in.readObject();

                if (!(object instanceof Request)) {
                    write(Response.fail("Gói tin gửi lên không đúng định dạng Request"));
                    continue;
                }

                Request request = (Request) object;
                Response response = handleRequest(request);
                write(response);
            }
        } catch (EOFException e) {
            System.out.println("[DISCONNECT] Client đã thoát.");
        } catch (Exception e) {
            System.err.println("[CLIENT ERROR] " + e.getMessage());
        } finally {
            close();
        }
    }

    /*
handleRequest() là hàm phân loại yêu cầu từ client.

Client gửi lên một Request có command.
Ví dụ:
- command = "LOGIN" thì gọi handleLogin().
- command = "SEARCH" thì gọi SongDAO.searchSongs().
- command = "ADD" thì kiểm tra Admin rồi gọi SongDAO.addSong().

Đây là trung tâm xử lý chức năng ở phía server.
*/

    private Response handleRequest(Request request) {
        try {
            String command = request.getCommand();

            if ("LOGIN".equals(command)) {
                return handleLogin(request);
            }

            if ("REGISTER".equals(command)) {
                return handleRegister(request);
            }

            if (currentUser == null) {
                return Response.fail("Bạn phải đăng nhập trước khi sử dụng hệ thống.");
            }

            switch (command) {
                case "GET_ALL":
                    return Response.ok("Lấy danh sách bài hát thành công", songDAO.getAllSongs());

                case "SEARCH":
                    String keyword = String.valueOf(request.getData()).trim();
                    historyDAO.saveKeyword(currentUser.getId(), keyword);
                    return Response.ok("Tìm kiếm thành công", songDAO.searchSongs(keyword));

                case "ADD":
                    requireAdmin();
                    return Response.ok("Thêm bài hát thành công", songDAO.addSong((Song) request.getData()));

                case "UPDATE":
                    requireAdmin();
                    return Response.ok("Cập nhật bài hát thành công", songDAO.updateSong((Song) request.getData()));

                case "DELETE":
                    requireAdmin();
                    return Response.ok("Xóa bài hát thành công", songDAO.deleteSong(String.valueOf(request.getData())));

                case "EXPORT_TXT":
                    FileIOUtil.saveSongsToTxt(songDAO.getAllSongs(), "backup_songs.txt");
                    return Response.ok("Đã xuất file backup_songs.txt");

                case "IMPORT_TXT":
                    requireAdmin();
                    List<Song> importedSongs = FileIOUtil.loadSongsFromTxt("backup_songs.txt");
                    for (Song song : importedSongs) {
                        songDAO.upsertSong(song);
                    }
                    return Response.ok("Đã nhập " + importedSongs.size() + " bài hát từ backup_songs.txt");

                case "EXPORT_XML":
                    XMLUtil.exportSongsToXML(songDAO.getAllSongs(), "backup_songs.xml");
                    return Response.ok("Đã xuất file backup_songs.xml");

                case "GET_HISTORY":
                    return Response.ok("Lấy lịch sử tìm kiếm thành công",
                            historyDAO.findByUser(currentUser.getId()));

                case "CLEAR_HISTORY":
                    historyDAO.clearByUser(currentUser.getId());
                    return Response.ok("Đã xóa lịch sử tìm kiếm");

                case "PLAYLISTS":
                    return Response.ok("Lấy playlist thành công",
                            playlistDAO.findByUser(currentUser.getId()));

                case "PLAYLIST_CREATE":
                    return Response.ok("Tạo playlist thành công",
                            playlistDAO.createPlaylist(currentUser.getId(), String.valueOf(request.getData())));

                case "PLAYLIST_DELETE":
                    return Response.ok("Xóa playlist thành công",
                            playlistDAO.deletePlaylist(currentUser.getId(), (Integer) request.getData()));

                case "PLAYLIST_ADD_SONG": {
                    String[] parts = String.valueOf(request.getData()).split("\\|", -1);
                    int playlistId = Integer.parseInt(parts[0]);
                    String songId = parts[1];
                    return Response.ok("Đã thêm bài hát vào playlist",
                            playlistDAO.addSongToPlaylist(currentUser.getId(), playlistId, songId));
                }

                case "PLAYLIST_REMOVE_SONG": {
                    String[] parts = String.valueOf(request.getData()).split("\\|", -1);
                    int playlistId = Integer.parseInt(parts[0]);
                    String songId = parts[1];
                    return Response.ok("Đã xóa bài hát khỏi playlist",
                            playlistDAO.removeSongFromPlaylist(currentUser.getId(), playlistId, songId));
                }

                case "PLAYLIST_SONGS":
                    return Response.ok("Lấy bài hát trong playlist thành công",
                            playlistDAO.getSongsInPlaylist(currentUser.getId(), (Integer) request.getData()));

                default:
                    return Response.fail("Lệnh không tồn tại: " + command);
            }
        } catch (SecurityException e) {
            return Response.fail(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail("Server xử lý lỗi: " + e.getMessage());
        }
    }

    private Response handleLogin(Request request) throws Exception {
        String[] data = (String[]) request.getData();

        if (data.length < 2) {
            return Response.fail("Thiếu username hoặc password");
        }

        User user = userDAO.authenticate(data[0], data[1]);

        if (user == null) {
            return Response.fail("Sai tài khoản hoặc mật khẩu");
        }

        this.currentUser = user;
        return Response.ok("Đăng nhập thành công", user);
    }

    private Response handleRegister(Request request) throws Exception {
        String[] data = (String[]) request.getData();

        if (data.length < 2) {
            return Response.fail("Thiếu tên đăng nhập hoặc mật khẩu.");
        }

        String username = data[0].trim();
        String password = data[1].trim();

        if (username.isEmpty() || password.isEmpty()) {
            return Response.fail("Tên đăng nhập và mật khẩu không được để trống.");
        }

        if (username.length() < 4) {
            return Response.fail("Tên đăng nhập phải có ít nhất 4 ký tự.");
        }

        if (password.length() < 6) {
            return Response.fail("Mật khẩu phải có ít nhất 6 ký tự.");
        }

        if (userDAO.usernameExists(username)) {
            return Response.fail("Tên đăng nhập đã tồn tại.");
        }

        boolean success = userDAO.registerUser(username, password);

        if (success) {
            return Response.ok("Đăng ký thành công. Bạn có thể đăng nhập.");
        }

        return Response.fail("Đăng ký thất bại.");
    }

    /*
requireAdmin() dùng để kiểm tra phân quyền.

Nếu currentUser không phải ADMIN thì server từ chối thao tác.
Điểm quan trọng: kiểm tra quyền ở server an toàn hơn chỉ khóa nút trên giao diện.
Vì nếu user tự gửi request trái phép, server vẫn chặn lại.
*/

    private void requireAdmin() {
        if (currentUser == null || !currentUser.isAdmin()) {
            throw new SecurityException("Bạn không có quyền ADMIN để thực hiện chức năng này.");
        }
    }

    private void write(Response response) throws Exception {
        out.writeObject(response);
        out.flush();
        out.reset();
    }

    private void close() {
        try {
            if (in != null) {
                in.close();
            }
        } catch (Exception ignored) {
        }

        try {
            if (out != null) {
                out.close();
            }
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
CLIENT HANDLER - XỬ LÝ REQUEST TỪ CLIENT
============================================================

Class này xử lý một client kết nối tới server.

Kiến thức áp dụng:
1. Multithreading:
   - ClientHandler implements Runnable.
   - Hàm run() là phần chạy trong luồng riêng.

2. Networking:
   - Nhận Request từ client bằng ObjectInputStream.
   - Gửi Response về client bằng ObjectOutputStream.

3. Security:
   - Kiểm tra đăng nhập.
   - Kiểm tra quyền Admin bằng requireAdmin().

4. JDBC:
   - Gọi các DAO như SongDAO, UserDAO, PlaylistDAO, SearchHistoryDAO để thao tác database.

Luồng chạy:
1. Client gửi Request lên server.
2. ClientHandler đọc Request.
3. handleRequest() kiểm tra command.
4. Tùy command mà gọi DAO hoặc Util tương ứng.
5. Server trả Response về client.

Các command quan trọng:
- LOGIN: đăng nhập.
- REGISTER: đăng ký tài khoản.
- GET_ALL: lấy tất cả bài hát.
- SEARCH: tìm kiếm bài hát.
- ADD/UPDATE/DELETE: thêm/sửa/xóa bài hát, chỉ Admin.
- EXPORT_TXT / IMPORT_TXT: ghi/đọc file TXT.
- EXPORT_XML: xuất XML.
- PLAYLIST_CREATE / PLAYLIST_ADD_SONG: quản lý playlist.
- GET_HISTORY / CLEAR_HISTORY: lịch sử tìm kiếm.

Câu trả lời khi thầy hỏi:
"ClientHandler là nơi server nhận yêu cầu từ client, xử lý chức năng và trả kết quả về. Vì mỗi ClientHandler chạy trong một luồng riêng nên nhiều client có thể dùng cùng lúc."
*/
