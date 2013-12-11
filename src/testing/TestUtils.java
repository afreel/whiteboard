package testing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import client.WhiteboardFrontEnd;

public class TestUtils {
    private Socket clientSocket;
    private List<String> serverReceivedMessages = new ArrayList<String>();
    private final long startTime = System.currentTimeMillis();

    /**
     * Generates a server
     * 
     * @throws IOException
     */
    @SuppressWarnings("resource")
    public void startServer(final int portNo) throws IOException {
        new Thread(new Runnable() {
            public void run() {
                ServerSocket serverSocket = null;

                try {
                    serverSocket = new ServerSocket(portNo);
                    System.out.println("Started server");
                    clientSocket = serverSocket.accept();
                    System.out.println("Accepted a client");

                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                new Thread(new Runnable() {
                    public void run() {
                        String inputLine;
                        BufferedReader in = null;
                        try {
                            in = new BufferedReader(new InputStreamReader(
                                    clientSocket.getInputStream()));
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        try {
                            while ((inputLine = in.readLine()) != null) {
                                serverReceivedMessages.add(inputLine);
                                long endTime = System.currentTimeMillis();
                                long totalTime = endTime - startTime;
                                System.out.println(serverReceivedMessages);
                                System.out.println("Received " + inputLine
                                        + " @ " + totalTime);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                clientSocket.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
        }).start();
    }


    public PrintWriter spawnClient(final String username, String whiteboard, final int portNo)
            throws UnknownHostException, IOException {
        String host = "localhost";

        // Connect to the socket
        System.out.println("attempting to connect to socket");
        final Socket socket = new Socket(host, portNo);
        System.out.println(username + ": socket connection established");

        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Tell the server we want to connect to a specific whiteboard
        out.println("whiteboard " + whiteboard + " username " + username);

        // Have a thread constantly listen for server messages
        new Thread(new Runnable() {
            public void run() {
                // Have a loop constantly checking for new messages from the
                // server.
                try {
                    String inputLine;
                    while ((inputLine = in.readLine()) != null && socket.isConnected()) {
                        System.out.println(username + ": Recieved message '"
                                + inputLine + "'");
                    }
                } catch (IOException e) {
                    e.printStackTrace(); // but don't terminate server
                } finally {
                    try {
                        // Close connection with server
                        System.out.println(username
                                + ": Closing connection with server");
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        
        return out;
    }
      
    /**
     * dummyFrontEnd allows us to call the models constructor without needing the class
     * WhieboardGUI.
     */
    public class dummyFrontEnd implements WhiteboardFrontEnd {
        public void drawLineOnGUI(String strx1, String stry1, String strx2,
                String stry2, String strwidth, String strr, String strg,
                String strb, String user) {
        }

        public void fillWithWhite() {
        }

        public void addNewUser(String user) {
        }

        public void removeUser(String user) {
        }

        public void loadGuiUsers(List<String> usersList) {
        }

        public void loadUsernameTakenImage() {           
        }

        public void loadConnectedToServerImage() {
        }
    }
}
