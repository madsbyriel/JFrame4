import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import classes.RequestReader;
import interfaces.IRequestReader;
import interfaces.RequestInfo;

public class Main {
    public static void main(String[] args) {
        RequestInfo info = null;
        try (ServerSocket server = new ServerSocket(31415)) {
            Socket clientSocket = server.accept();
            IRequestReader reader = new RequestReader(clientSocket.getInputStream());
            info = reader.readRequest();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (info != null) {
            System.out.println(info.getContentLength());
            for (String key : info.getKeys()) {
                System.out.println(key + ": " + info.getValue(key));
            }
        }
    }
}