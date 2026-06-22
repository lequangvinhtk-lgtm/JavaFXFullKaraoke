package com.vku.karaoke.client;

public class Launcher {
    public static void main(String[] args) {
        MainClientApp.main(args);
    }
}
//
///model      -> OOP, định nghĩa đối tượng Song, User, Playlist, SearchHistory
///server     -> chạy server, xử lý đa luồng và request từ client
///server/dao -> JDBC, thao tác database
///client     -> giao diện JavaFX
///utils      -> DBUtil, FileIOUtil, XMLUtil, PasswordUtil, ConfigXMLUtil