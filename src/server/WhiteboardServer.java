package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * Main server class. An instance of this class will listen on a specific port
 * for new connecting clients. For each connected client, it will spin a thread
 * which waits for the client to select their whiteboard and username. Once the
 * client chooses a whiteboard and username, the client is associated with that
 * whiteboard, and a new thread is spun to listen for changes the client makes
 * to his local whiteboard.
 * 
 */

/*
 * Rep invariant: - whiteboardMap != null - whiteboardMap.size == 5 - usernames
 * != null
 */

/*----------------------------------------------------------Thread-safety Argument-------------------------------------------------------//
 * A new thread is spun for every client that connects to the server, and those threads independently handle all messages sent from that 
 * client via the ClientHandler class. 
 * 
 * On receipt of a message, ClientHandler calls handleMessage, which acquires a lock on its whiteboardID field, which indicates which
 * whiteboard that client is connected to. The only mutator method in ClientHandler (changeWhiteboardID) must also acquire a lock on
 * whiteboardID, so by use of the Monitor Pattern we have avoided any race conditions on whiteboardID. Aside from that, handleMessage
 * only accesses whiteboardMap (as an observer) and calls methods on Whiteboard. As whiteboardMap is only ever accessed by observer calls,
 * multi-threaded access to it does not threaten its intended functionality and is therefore threadsafe. As Whiteboard is threadsafe,
 * actions here from ClientHandler (handleMessage) thus preserve the thread-safety of our datatype as a whole. 
 * 
 */
public class WhiteboardServer {
    private final HashMap<String, Whiteboard> whiteboardMap;
    private final List<String> usernames;

    @SuppressWarnings("serial")
    public WhiteboardServer() {
        this.whiteboardMap = new HashMap<String, Whiteboard>() {
            {
                put("1", new Whiteboard());
                put("2", new Whiteboard());
                put("3", new Whiteboard());
                put("4", new Whiteboard());
                put("5", new Whiteboard());
            }
        };
        this.usernames = new ArrayList<String>();
    }

    /**
     * This method will listen for any new connecting clients. New clients who
     * successfully connect will be spun their own thread, which wait for them
     * to chose their desired whiteboard and username. On receipt of these
     * values, the thread will create a new Client object and add that object to
     * the clients list of the user's chosen whiteboard.
     * 
     * @param port
     *            the port this server will listen on
     * @throws IOException
     *             if an error occurs when this tries to accept a new port
     */
    @SuppressWarnings("resource")
    public void serve(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);

        while (true) {
            final Socket socket = serverSocket.accept();

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        boolean connectedToWhiteboard = false; // client has not
                                                               // yet
                                                               // successfully
                                                               // connected to a
                                                               // whiteboard
                        BufferedReader clientIn = new BufferedReader(
                                new InputStreamReader(socket.getInputStream()));
                        PrintWriter clientWriter = new PrintWriter(socket
                                .getOutputStream(), true);
                        while (!connectedToWhiteboard) {
                            String inputLine = clientIn.readLine();
                            while (inputLine == null) { // Wait for the client
                                                        // to send a message
                                inputLine = clientIn.readLine();
                            }
                            // Extract desired information from the client's
                            // message
                            String[] inputAsArray = inputLine.split(" ");
                            if (inputAsArray[0].equals("whiteboard")) {
                                if (!usernames.contains(inputAsArray[3])) {
                                    String chosenWhiteboard = inputAsArray[1];
                                    String username = inputAsArray[3];
                                    Client newClient = new Client(username,
                                            clientWriter);
                                    whiteboardMap.get(chosenWhiteboard)
                                            .addClient(newClient);
                                    usernames.add(username);

                                    Thread handleClient = new Thread(
                                            new ClientHandler(socket,
                                                    chosenWhiteboard, newClient));
                                    handleClient.start();
                                    connectedToWhiteboard = true; // client has
                                                                  // now
                                                                  // connected
                                                                  // to a
                                                                  // whiteboard
                                } else {
                                    clientWriter.println("usernameTaken");
                                }
                            }
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            thread.start();
        }
    }

    /**
     * 
     * This will handle all messages sent from a given client.
     * 
     */
    class ClientHandler implements Runnable {
        private final Socket socket;
        private String whiteboardID;
        private final Client client;

        /**
         * Instantiates a new ClientHandler instance. This will listen to a
         * client and handle all messages that client sends.
         * 
         * @param socket
         *            client's socket
         * @param whiteboardID
         *            id of the whiteboard that client has chosen to connect to
         * @param client
         *            the client that this ClientHandler instance listens for
         */
        public ClientHandler(Socket socket, String whiteboardID, Client client) {
            this.socket = socket;
            this.whiteboardID = whiteboardID;
            this.client = client;
        }

        @Override
        public void run() {
            try {
                BufferedReader clientIn = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));

                String inputLine = clientIn.readLine();
                while (true) {
                    while (inputLine == null) {
                        inputLine = clientIn.readLine();
                    }
                    handleMessageFromClient(inputLine);
                    inputLine = clientIn.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * Process the message sent from the client.
         * 
         * @param message
         *            message sent from client.
         */
        private void handleMessageFromClient(String message) {
            synchronized (whiteboardID) {
                String[] messageAsArray = message.split(" ");
                switch (messageAsArray[0]) {
                case "line": // Client has drawn a new line
                    whiteboardMap.get(whiteboardID).addLine(
                            message + " " + this.client.getUsername());
                    break;
                case "whiteboard": // Client has chosen to connect to another
                                   // whiteboard
                    whiteboardMap.get(whiteboardID).removeClient(this.client);
                    changeWhiteboardID(messageAsArray[1]);
                    whiteboardMap.get(whiteboardID).addClient(this.client);
                    break;
                case "disconnect": // Client has closed their window, and is now
                                   // disconnected from the server
                    whiteboardMap.get(whiteboardID).removeClient(this.client);
                    Iterator<String> iter = usernames.iterator();
                    while (iter.hasNext()) {
                        if (iter.next().equals(this.client.getUsername())) {
                            iter.remove();
                            break;
                        }
                    }
                    break;
                }
            }

        }

        /**
         * mutator method to update our whiteboardID variable. For use when the
         * client switches whiteboards.
         * 
         * @param newWhiteboard
         *            id of new whiteboard.
         */
        private void changeWhiteboardID(String newWhiteboard) {
            synchronized (whiteboardID) {
                this.whiteboardID = newWhiteboard;
            }
        }
    }

    /**
     * Ensure our representation invariant is being maintained
     */
    public void checkRep() {
        assert (whiteboardMap != null);
        assert (whiteboardMap.size() == 5);
        assert (usernames != null);
    }

    public static void main(String[] args) throws IOException {
        final WhiteboardServer server = new WhiteboardServer();
        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    server.serve(4444);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

}
