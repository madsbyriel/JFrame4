import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import classes.RequestReader;
import classes.RequestResponder;
import interfaces.IRequestReader;
import interfaces.IRequestResponder;
import interfaces.RequestInfo;

public class Main {
    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(31415)) {
            while (true) {
                Socket clientSocket = server.accept();
                System.out.println("Accepted new connection: " + clientSocket.getInetAddress());
                ConnectionHandler handler = new ConnectionHandler(clientSocket);
                handler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ConnectionHandler extends Thread {
        private Socket clientSocket;

        public ConnectionHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        public void run() {
            IRequestReader reader = null;

            try {
                reader = new RequestReader(clientSocket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (reader == null) {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }

            while (true) {
                RequestInfo info = reader.readRequest();
                if (info == null) {
                    continue;
                }

                info.printKeyValuePairsQuery();

                IRequestResponder responder = new RequestResponder(clientSocket); 
                int code = responder.respondRequest(info);
                if (code == -1) {
                    try {
                        clientSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }
            }
        }
    }
}