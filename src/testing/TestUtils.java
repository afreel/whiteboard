package testing;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

import client.WhiteboardFrontEnd;

/*
 * 
 * 
 */
public class TestUtils {
    private Socket clientSocket;
    private PrintWriter socketOut;
    public List<String> serverReceivedMessages = new ArrayList<String>();
    public ArrayList<ArrayList<String>> clientReceivedMessages = new ArrayList<ArrayList<String>>();
    public List<String> guiReceivedMessages = new ArrayList<String>();

    /**
     * Generate a server on localhost at a given port. This "dummy" server
     * allows any number of users to connect to it. When a user connects, a
     * serverListener is run for that user in particular, and listens for any
     * messages that the client would send to the server, and stores it in a
     * serverReceivedMessages list that can be accessed publicly from a testing
     * method.
     * 
     * @param portNo
     *            port where we want to generate server
     * @throws IOException
     *             if input/output error
     */
    public void startServer(final int portNo) throws IOException {
        new Thread(new Runnable() {
            public void run() {
                ServerSocket serverSocket = null;

                try {
                    serverSocket = new ServerSocket(portNo);

                    while (true) {
                        clientSocket = serverSocket.accept();
                        socketOut = new PrintWriter(
                                clientSocket.getOutputStream(), true);

                        new Thread(new serverListener()).start();
                    }

                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }
        }).start();
    }

    /**
     * Sends a message to the client that most recently connected to the server
     * 
     * @param message
     *            the message to send to the clients input stream.
     */
    public void serverSendToClient(String message) {
        socketOut.println(message);
    }

    /**
     * Generates a "dummy" client and returns a PrintWriter that can write to
     * that dummy client's inputstream.
     * 
     * @param username
     *            the name of the client
     * @param whiteboard
     *            the whiteboard we want to connect to
     * @param portNo
     *            the port we will try to connect to
     * @return a PrintWriter that allows anybody to send messages to the client
     * @throws UnknownHostException
     *             if can't find host
     * @throws IOException
     *             if I/O error
     */
    public PrintWriter spawnClient(final String username, String whiteboard,
            final int portNo) throws UnknownHostException, IOException {
        String host = "localhost";
        final int id = clientReceivedMessages.size();

        // Connect to the socket
        final Socket socket = new Socket(host, portNo);

        clientReceivedMessages.add(new ArrayList<String>());

        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        final BufferedReader in = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));

        // Tell the server we want to connect to a specific whiteboard
        out.println("whiteboard " + whiteboard + " username " + username);

        // Have a thread constantly listen for server messages
        new Thread(new Runnable() {
            public void run() {
                // Have a loop constantly checking for new messages from the
                // server.
                try {
                    String inputLine;
                    while ((inputLine = in.readLine()) != null
                            && socket.isConnected()) {
                        clientReceivedMessages.get(id).add(inputLine);
                    }
                } catch (IOException e) {
                    e.printStackTrace(); // but don't terminate server
                } finally {
                    try {
                        // Close connection with server
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        sleep();
        return new PrintWriter(socketOut, true);
    }

    /**
     * dummyFrontEnd allows us to call whiteboardModel's constructor without
     * needing the class WhiteboardGUI.
     */
    public class dummyFrontEnd implements WhiteboardFrontEnd {

        public void drawLineOnGUI(String strx1, String stry1, String strx2,
                String stry2, String strwidth, String strr, String strg,
                String strb, String user) {
            guiReceivedMessages.add("line " + strx1 + " " + stry1 + " " + strx2
                    + " " + stry2 + " " + strwidth + " " + strr + " " + strg
                    + " " + strb + " " + user);
        }

        public void fillWithWhite() {
        }

        public void addNewUser(String user) {
            guiReceivedMessages.add("newUser " + user);
        }

        public void removeUser(String user) {
            guiReceivedMessages.add("users " + user);
        }

        public void loadGuiUsers(List<String> usersList) {
            String string = "";
            for (String user : usersList) {
                string += " " + user;
            }
            guiReceivedMessages.add("users" + string);
        }

        public void loadUsernameTakenImage() {
        }

        public void loadConnectedToServerImage() {
        }
    }

    /**
     * Give the server Thread some time to process the message to prevent race
     * conditions.
     */
    public void sleep() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Listens for server messages, and stores them in serverReceivedMessages
     */
    private class serverListener implements Runnable {
        @Override
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
    }
}
