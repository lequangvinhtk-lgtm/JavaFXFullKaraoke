package com.vku.karaoke.utils;

import com.vku.karaoke.model.SystemConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    private DBUtil() {
    }

    public static Connection getConnection() throws SQLException {
        SystemConfig config = ConfigXMLUtil.loadConfig();

        String url = "jdbc:mysql://" + config.getDbHost() + ":" + config.getDbPort()
                + "/" + config.getDbName()
                + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
                + "&characterEncoding=utf8";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Chưa có thư viện mysql-connector-j trong pom.xml", e);
        }

        return DriverManager.getConnection(url, config.getDbUser(), config.getDbPassword());
    }
}
/*
============================================================
DBUTIL - KẾT NỐI DATABASE BẰNG JDBC
============================================================

Class này dùng để tạo kết nối từ Java tới MySQL.

Kiến thức áp dụng:
JDBC (Java Database Connectivity - kết nối Java với cơ sở dữ liệu).

Câu lệnh quan trọng:
- Class.forName("com.mysql.cj.jdbc.Driver"): nạp driver MySQL.
- DriverManager.getConnection(url, user, password): tạo kết nối database.

Luồng chạy:
1. Đọc config.xml bằng ConfigXMLUtil.
2. Lấy dbHost, dbPort, dbName, dbUser, dbPassword.
3. Tạo chuỗi URL kết nối MySQL.
4. Trả về Connection cho các DAO sử dụng.

Câu trả lời khi thầy hỏi:
"DBUtil là lớp trung gian tạo kết nối JDBC. Các DAO không tự viết lại kết nối mà gọi DBUtil.getConnection(), giúp code gọn và dễ bảo trì."
*/