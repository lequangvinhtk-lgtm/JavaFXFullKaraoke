package com.vku.karaoke.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class PasswordUtil {
    private PasswordUtil() {
    }

    /*
sha256() nhận mật khẩu dạng String,
sau đó dùng MessageDigest để tạo chuỗi hash SHA-256.
*/
    public static String sha256(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encoded = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : encoded) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Không thể mã hóa mật khẩu SHA-256", e);
        }
    }
}
/*
============================================================
PASSWORD UTIL - HASH MẬT KHẨU
============================================================

Class này áp dụng Java Security.

Kiến thức áp dụng:
- MessageDigest: lớp Java dùng để hash dữ liệu.
- SHA-256: thuật toán hash 256-bit.
- StandardCharsets.UTF_8: mã hóa chuỗi thành byte.

Hash là gì?
Hash là biến mật khẩu thành một chuỗi khó đọc ngược.
Ví dụ password "admin123" sẽ được chuyển thành chuỗi hash dài.

Lý do dùng hash:
Không nên lưu mật khẩu thật trong database.
Khi đăng nhập, chương trình hash mật khẩu người dùng nhập rồi so sánh với password_hash trong database.

Câu trả lời khi thầy hỏi:
"Em không lưu mật khẩu trực tiếp. Em dùng SHA-256 để hash mật khẩu rồi mới lưu hoặc so sánh."
*/