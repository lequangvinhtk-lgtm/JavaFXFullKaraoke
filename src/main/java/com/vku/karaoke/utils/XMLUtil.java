package com.vku.karaoke.utils;

import com.vku.karaoke.model.Song;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.List;

public class XMLUtil {
    private XMLUtil() {
    }

    public static void exportSongsToXML(List<Song> songs, String filePath) throws Exception {
        Document doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .newDocument();

        Element root = doc.createElement("karaokeSongs");
        doc.appendChild(root);

        for (Song song : songs) {
            Element songNode = doc.createElement("song");
            songNode.setAttribute("id", song.getId());

            append(doc, songNode, "title", song.getTitle());
            append(doc, songNode, "artist", song.getArtist());
            append(doc, songNode, "genre", song.getGenre());

            root.appendChild(songNode);
        }

        javax.xml.transform.Transformer transformer =
                TransformerFactory.newInstance().newTransformer();

        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        transformer.transform(new DOMSource(doc), new StreamResult(new File(filePath)));
    }

    private static void append(Document doc, Element parent, String tag, String value) {
        Element element = doc.createElement(tag);
        element.appendChild(doc.createTextNode(value == null ? "" : value));
        parent.appendChild(element);
    }
}


/*
============================================================
XML UTIL - XUẤT DANH SÁCH BÀI HÁT RA XML
============================================================

Class này áp dụng XML DOM để tạo file XML.

Kiến thức áp dụng:
- Document: tài liệu XML.
- Element: thẻ XML.
- Transformer: ghi XML ra file.
- DOM: biểu diễn XML dạng cây.

Chức năng:
exportSongsToXML() xuất danh sách bài hát ra backup_songs.xml.

Cấu trúc XML:
<karaokeSongs>
    <song id="S001">
        <title>...</title>
        <artist>...</artist>
        <genre>...</genre>
    </song>
</karaokeSongs>

Câu trả lời khi thầy hỏi:
"Em dùng XML để export danh sách bài hát ra file XML, giúp sao lưu hoặc chia sẻ dữ liệu ở dạng có cấu trúc."
*/
