package com.vku.karaoke.utils;

import com.vku.karaoke.model.Song;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileIOUtil {
    private FileIOUtil() {
    }

    /*
saveSongsToTxt() ghi danh sách bài hát ra file TXT.

Mỗi bài hát được ghi thành một dòng.
Các thông tin cách nhau bằng dấu | để lúc đọc lại dễ tách dữ liệu.
*/
    public static void saveSongsToTxt(List<Song> songs, String filePath) throws Exception {
        Path path = Path.of(filePath);

        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            for (Song song : songs) {
                writer.write(song.getId() + "|" + song.getTitle() + "|" + song.getArtist() + "|" + song.getGenre());
                writer.newLine();
            }
        }
    }

    /*
loadSongsFromTxt() đọc file TXT từng dòng.

Mỗi dòng được split theo dấu | để lấy:
- id
- title
- artist
- genre

Sau đó tạo object Song và đưa vào danh sách.
*/
    public static List<Song> loadSongsFromTxt(String filePath) throws Exception {
        List<Song> songs = new ArrayList<>();
        Path path = Path.of(filePath);

        if (!Files.exists(path)) {
            return songs;
        }

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", -1);

                if (parts.length == 4) {
                    songs.add(new Song(
                            parts[0].trim(),
                            parts[1].trim(),
                            parts[2].trim(),
                            parts[3].trim()
                    ));
                }
            }
        }

        return songs;
    }
}

/*
============================================================
FILE IO UTIL - ĐỌC GHI FILE TXT
============================================================

Class này áp dụng IOStream để đọc/ghi file TXT.

Kiến thức áp dụng:
- BufferedWriter: ghi file.
- BufferedReader: đọc file.
- Files.newBufferedWriter(): tạo luồng ghi file.
- Files.newBufferedReader(): tạo luồng đọc file.
- try-with-resources: tự đóng file sau khi dùng.

Chức năng:
1. saveSongsToTxt(): xuất danh sách bài hát ra file backup_songs.txt.
2. loadSongsFromTxt(): đọc bài hát từ file backup_songs.txt.

Định dạng file TXT:
id|title|artist|genre

Ví dụ:
S001|Nơi này có anh|Sơn Tùng M-TP|Pop

Câu trả lời khi thầy hỏi:
"Em dùng IOStream để sao lưu danh sách bài hát ra TXT và đọc lại TXT để import vào database."
*/