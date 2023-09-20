package classes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import interfaces.IRequestResponder;
import interfaces.RequestInfo;

public class RequestResponder implements IRequestResponder {
    private static Map<String, File> availablePaths;
    private static String wwwRoot;
    private Socket clientSocket;

    static {
        wwwRoot = System.getProperty("user.dir") + "\\wwwRoot";
        availablePaths = new HashMap<>();
        setupPaths();

        for (String key : availablePaths.keySet()) {
            System.out.println(key + ": " + availablePaths.get(key).getAbsolutePath());
        }
    }


    public RequestResponder(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

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
