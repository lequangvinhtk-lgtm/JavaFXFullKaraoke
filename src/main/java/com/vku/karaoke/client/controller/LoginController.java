package com.vku.karaoke.client.controller;

import com.vku.karaoke.client.MainClientApp;
import com.vku.karaoke.client.network.ClientConnection;
import com.vku.karaoke.model.Request;
import com.vku.karaoke.model.Response;
import com.vku.karaoke.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private Label lblTitle;

    @FXML
    private Label lblSubtitle;

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private PasswordField txtConfirmPassword;

    @FXML
    private Label lblMessage;

    @FXML
    private Button btnSubmit;

    @FXML
    private Button btnSwitchMode;

    private boolean registerMode = false;

    @FXML
    public void initialize() {
        showLoginMode();
    }

    @FXML
    void handleSubmit(ActionEvent event) {
        if (registerMode) {
            handleRegister();
        } else {
            handleLogin(event);
        }
    }

    @FXML
    void handleSwitchMode(ActionEvent event) {
        if (registerMode) {
            showLoginMode();
        } else {
            showRegisterMode();
        }
    }

    private void handleLogin(ActionEvent event) {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Vui lòng nhập đầy đủ tài khoản và mật khẩu.");
            return;
        }

        ClientConnection connection = null;

        try {
            connection = new ClientConnection();

            Response response = connection.send(new Request(
                    "LOGIN",
                    new String[]{username, password}
            ));

            if (!response.isSuccess()) {
                showError(response.getMessage());
                connection.close();
                return;
            }

            User user = (User) response.getData();

            FXMLLoader loader = new FXMLLoader(MainClientApp.class.getResource("/dashboard.fxml"));
            Scene scene = new Scene(loader.load(), 1100, 720);
            scene.getStylesheets().add(MainClientApp.class.getResource("/style.css").toExternalForm());

            KaraokeDashboardController controller = loader.getController();
            controller.initUserData(connection, user);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Hệ thống quản lý bài hát Karaoke");
            stage.setScene(scene);
            stage.centerOnScreen();

        } catch (Exception e) {
            showError("Không kết nối được Server. Hãy chạy MainServer trước.");
            if (connection != null) {
                connection.close();
            }
            e.printStackTrace();
        }
    }

    private void handleRegister() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();
        String confirmPassword = txtConfirmPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showError("Vui lòng nhập đầy đủ thông tin đăng ký.");
            return;
        }

        if (username.length() < 4) {
            showError("Tên đăng nhập phải có ít nhất 4 ký tự.");
            return;
        }

        if (password.length() < 6) {
            showError("Mật khẩu phải có ít nhất 6 ký tự.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Mật khẩu nhập lại không khớp.");
            return;
        }

        ClientConnection connection = null;

        try {
            connection = new ClientConnection();

            Response response = connection.send(new Request(
                    "REGISTER",
                    new String[]{username, password}
            ));

            if (response.isSuccess()) {
                showSuccess(response.getMessage());
                txtPassword.clear();
                txtConfirmPassword.clear();
                showLoginMode();
                txtUsername.setText(username);
            } else {
                showError(response.getMessage());
            }

        } catch (Exception e) {
            showError("Không kết nối được Server. Hãy chạy MainServer trước.");
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    private void showLoginMode() {
        registerMode = false;

        lblTitle.setText("KARAOKE MANAGER");
        lblSubtitle.setText("Đăng nhập để quản lý bài hát, playlist và lịch sử tìm kiếm");

        txtConfirmPassword.setVisible(false);
        txtConfirmPassword.setManaged(false);

        btnSubmit.setText("Đăng nhập");
        btnSwitchMode.setText("Chưa có tài khoản? Đăng ký");

        lblMessage.setText("");
    }

    private void showRegisterMode() {
        registerMode = true;

        lblTitle.setText("ĐĂNG KÝ TÀI KHOẢN");
        lblSubtitle.setText("Tạo tài khoản User để sử dụng hệ thống Karaoke");

        txtConfirmPassword.setVisible(true);
        txtConfirmPassword.setManaged(true);

        btnSubmit.setText("Đăng ký");
        btnSwitchMode.setText("Đã có tài khoản? Đăng nhập");

        lblMessage.setText("");
        txtPassword.clear();
        txtConfirmPassword.clear();
    }

    private void showError(String message) {
        lblMessage.setStyle("-fx-text-fill: #dc2626; -fx-font-weight: bold;");
        lblMessage.setText(message);
    }

    private void showSuccess(String message) {
        lblMessage.setStyle("-fx-text-fill: #16a34a; -fx-font-weight: bold;");
        lblMessage.setText(message);
    }
}



/*
============================================================
LOGIN CONTROLLER - ĐĂNG NHẬP VÀ ĐĂNG KÝ
============================================================

Class này điều khiển màn hình đăng nhập/đăng ký JavaFX.

Kiến thức áp dụng:
- JavaFX Controller.
- Networking: gửi Request LOGIN/REGISTER lên server.
- Security: đăng nhập, đăng ký tài khoản.
- Validation: kiểm tra dữ liệu nhập.

Luồng đăng nhập:
1. User nhập username/password.
2. Client tạo Request("LOGIN", data).
3. ClientConnection gửi request lên server.
4. Server kiểm tra tài khoản trong database.
5. Nếu đúng, client mở Dashboard.

Luồng đăng ký:
1. User nhập username/password/confirm password.
2. Client kiểm tra dữ liệu.
3. Client gửi Request("REGISTER", data).
4. Server lưu tài khoản mới vào database với role USER.

Câu trả lời khi thầy hỏi:
"LoginController không xử lý database trực tiếp. Nó chỉ nhận dữ liệu từ giao diện và gửi request lên server."
*/