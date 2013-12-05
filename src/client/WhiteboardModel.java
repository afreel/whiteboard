package client;

import java.io.*;
import java.net.*;

public class WhiteboardModel {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    /**
     * Instantiates a model(back-end to the Whiteboard Client GUI)
     * 
     * @param host
     *            the host address of the server the clients wants to connect to
     * @param port
     *            the port number of the server
     * @throws UnknownHostException
     * @throws IOException
     */
    WhiteboardModel(String host, int port) {

        try {
            // Instantiate all finals
            socket = new Socket(host, port);
            // Out, accessed to send data to the server:
            out = new PrintWriter(socket.getOutputStream(), true);
            // In, accessed to get data from the server, need a for loop to get
            // check if it is ever updated:
            in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        new Thread(new Runnable() {
            public void run() {
                // Have a loop constantly checking for new messages from the
                // server.
                try {
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        System.out
                                .println("Received a message from the server: "
                                        + inputLine);
                    }
                } catch (IOException e) {
                    e.printStackTrace(); // but don't terminate server
                } finally {
                    try {
                        // Close connection with server
                        System.out.println("Closing connection with server");
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    public void sendMessageToServer(String message) {
        out.println(message);
    }
}
