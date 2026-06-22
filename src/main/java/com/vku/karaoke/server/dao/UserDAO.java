package com.vku.karaoke.server.dao;

import com.vku.karaoke.model.User;
import com.vku.karaoke.utils.DBUtil;
import com.vku.karaoke.utils.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLIntegrityConstraintViolationException;

public class UserDAO {

    // Đăng nhập tài khoản
    public User authenticate(String username, String password) throws Exception {
        String sql = "SELECT id, username, role FROM users WHERE username = ? AND password_hash = ?";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, PasswordUtil.sha256(password));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("role")
                    );
                }
            }
        }

        return null;
    }

    // Kiểm tra username đã tồn tại chưa
    public boolean usernameExists(String username) throws Exception {
        String sql = "SELECT id FROM users WHERE username = ?";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // Đăng ký tài khoản mới
    // Tài khoản đăng ký mới mặc định là USER, không phải ADMIN
    public boolean registerUser(String username, String password) throws Exception {
        String sql = "INSERT INTO users(username, password_hash, role) VALUES (?, ?, 'USER')";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, PasswordUtil.sha256(password));

            return ps.executeUpdate() > 0;

        } catch (SQLIntegrityConstraintViolationException e) {
            // Trường hợp username bị trùng
            return false;
        }
    }
}