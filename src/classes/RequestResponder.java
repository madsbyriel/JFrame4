package classes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import interfaces.IRequestResponder;
import interfaces.RequestInfo;

public class RequestResponder implements IRequestResponder {
    private static Map<String, File> availablePaths;
    private static Map<String, String> extensionToMime;
    private static String wwwRoot;
    private Socket clientSocket;

    static {
        extensionToMime = extensionToMimeTypeCreate();
        wwwRoot = System.getProperty("user.dir") + "\\wwwRoot";
        availablePaths = new HashMap<>();
        setupPaths();
    }

    // ------------------ STATIC METHODS ------------------ //
    private static void setupPaths() {
        File rootFile = new File(wwwRoot);
        iterateDirectory(rootFile);
    }

    private static void iterateDirectory(File dir) {
        for (File f : dir.listFiles()) {
            if (f.isDirectory()) {
                iterateDirectory(f);
                continue;
            }

            availablePaths.put(f.getAbsolutePath().substring(wwwRoot.length()), f);
        }
    }

    private static Map<String, String> extensionToMimeTypeCreate() {
        Map<String, String> map = new HashMap<>();
        map.put(".css", "text/css");
        map.put(".csv", "text/csv");
        map.put(".doc", "application/msword");
        map.put(".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        map.put(".gz", "application/gzip");
        map.put(".gif", "image/gif");
        map.put(".html", "text/html");
        map.put(".htm", "text/html");
        map.put(".ico", "image/vnd.microsoft.icon");
        map.put(".jar", "application/java-archive");
        map.put(".jpeg", "image/jpeg");
        map.put(".jpg", "image/jpeg");
        map.put(".js", "text/javascript");
        map.put(".json", "application/json");
        map.put(".jsonld", "application/ld+json");
        map.put(".mjs", "text/javascript");
        map.put(".mp3", "audio/mpeg");
        map.put(".mp4", "video/mp4");
        map.put(".png", "image/png");
        map.put(".pdf", "application/pdf");
        map.put(".php", "application/x-httpd-php");
        map.put(".ppt", "application/vnd.ms-powerpoint");
        map.put(".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
        map.put(".rar", "application/vnd.rar");
        map.put(".rtf", "application/rtf");
        map.put(".sh", "application/x-sh");
        map.put(".svg", "image/svg+xml");
        map.put(".tar", "application/x-tar");
        map.put(".ts", "video/mp2t");
        map.put(".txt", "text/plain");
        map.put(".wav", "audio/wav");
        map.put(".xhtml", "application/xhtml+xml");
        map.put(".xls", "application/vnd.ms-excel");
        map.put(".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        map.put(".xml", "application/xml");
        map.put(".zip", "application/zip");
        map.put(".7z", "application/x-7z-compressed");
        return map;
    }

    // ------------------ MEMBER METHODS ------------------ //
    @Override
    public int respondRequest(RequestInfo info) {
        try {
            if (isGet(info)) return handleGet(info);
            if (isPost(info)) return handlePost(info);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return handleUnknown(info);
    }

    public RequestResponder(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    private int handleUnknown(RequestInfo info) {
        return -1;
    }

    private int handlePost(RequestInfo info) {
        return -1;
    }

    private int handleGet(RequestInfo info) throws IOException {
        File f = availablePaths.get(info.getPath());
        if (f == null) {
            sendNotFound();
            System.out.println("404: " + info.getPath());
            return -1;
        }

        sendOK(f);

        System.out.println("GET: " + info.getPath());
        return 1;
    }

    private boolean isGet(RequestInfo info) {
        return info.getMethod().equals("GET");
    }

    private boolean isPost(RequestInfo info) {
        return info.getMethod().equals("POST");
    }

    private void sendOK(File f) throws IOException {
        String header = "" +
            "HTTP/1.1 200 OK\r\n" +
            "Content-Length: " + f.length() + "\r\n\r\n";
        sendFileAndHeader(header, f);
    }

    private void sendFileAndHeader(String header, File f) throws IOException {
        byte[] buffer = new byte[10000];
        int readBytesTotal = 0;
        int readBytes;
        
        FileInputStream fs = new FileInputStream(f);
        OutputStream oStream = clientSocket.getOutputStream();
        oStream.write(header.getBytes("UTF-8"));
        while (readBytesTotal < f.length()) {
            readBytes = fs.read(buffer);
            readBytesTotal += readBytes;
            oStream.write(buffer, 0, readBytes);
        }
        fs.close();
    }

    private void sendNotFound() throws IOException {
        File f = new File(wwwRoot + "\\404.html");
        String header = "" + 
            "HTTP/1.1 404 Not Found" +
            "Content-Length: " + f.length() + "\r\n\r\n";
        sendFileAndHeader(header, f);
    }
}
