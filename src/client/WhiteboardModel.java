package client;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * WhiteboardModel is the back-end of the Whiteboard on the client side. It is
 * responsible for connecting the client with the server and listening for
 * server messages, and execute the expected behavior from the given message.
 */
public class WhiteboardModel {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private WhiteboardFrontEnd gui;
    private List<String> usersList = new ArrayList<String>();

    /**
     * Instantiates a model with the associated GUI. The associated GUI needs to
     * have the methods as specified in the WhiteboardFrontEnd interface, to be
     * able to handle each of the messages.
     * 
     * @param associatedGUI
     */
    public WhiteboardModel(WhiteboardFrontEnd associatedGUI) {
        gui = associatedGUI;
    }

    /**
     * Opens a socket connection with a server located @ the given host and
     * port, and instantiates the in and out fields.
     * 
     * @param host
     *            the name of the host
     * @param port
     *            the port number
     * @throws IOException
     *             if can't connect to the server
     */
    public void connectToServer(String host, int port) throws IOException {
        // Connecting to the server
        socket = new Socket(host, port);

        // Store the output stream of the socket, accessed to send messages to
        // the server:
        out = new PrintWriter(socket.getOutputStream(), true);

        // Store the input stream of the server
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Have a thread constantly listen for server messages
        new Thread(new serverListener()).start();
    }

    /**
     * Sends a message through the output stream to the server
     * 
     * @param message
     *            a string, following the established protocol
     */
    private void sendMessageToServer(String message) {
        out.println(message);
    }

    public void disconnectFromServer() {
        sendMessageToServer("disconnect");
    }

    /**
     * Sends a connect to whiteboard message to the server in order for the
     * server to associate this user with the specified whiteboard
     * 
     * @param whiteboard
     *            a string containing their ID number
     * @param username
     *            a string of any characters representing the users username.
     */
    public void connectToWhiteBoard(String whiteboard, String username) {
        sendMessageToServer("whiteboard " + whiteboard + " username "
                + username);
    }

    /**
     * Sends a draw message to the server with the parameters described in the
     * protocol:
     * 
     * @param x1
     *            x of the first point in the line
     * @param y1
     *            y of the first point in the line
     * @param x2
     *            x of the second point in the line
     * @param y2
     *            y of the second point in the line
     * @param width
     *            of the line
     * @param r
     *            red component of the color of the line. Integer within the
     *            range [0,255]
     * @param g
     *            green component of the color of the line. Integer within the
     *            range [0,255]
     * @param b
     *            blue component of the color of the line. Integer within the
     *            range [0,255]
     */
    public void drawLineOnServer(int x1, int y1, int x2, int y2, int width,
            int r, int g, int b) {
        sendMessageToServer("line " + Integer.toString(x1) + " "
                + Integer.toString(y1) + " " + Integer.toString(x2) + " "
                + Integer.toString(y2) + " " + Integer.toString(width) + " "
                + Integer.toString(r) + " " + Integer.toString(g) + " "
                + Integer.toString(b));
    }

    /**
     * Given a list of users, updates the front end side with this list
     * 
     * @param usersArray
     *            , a list of users
     */
    private void updateUsersList(String[] usersArray) {
        usersList = new ArrayList<String>();

        for (int i = 1; i < usersArray.length; i++) {
            usersList.add(usersArray[i]);
        }

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                gui.loadGuiUsers(usersList);
            }
        });
    }

    /**
     * This methods checks a message for what type it is(according to the
     * protocol), and executes the associated method on the front end side
     * 
     * @param message
     *            a message following the protocol
     */
    private void handleMessage(String message) {
        // Separate the message to get each argument in the message:
        final String[] messageAsArray = message.split(" ");

        switch (messageAsArray[0]) {

        case "users":
            updateUsersList(messageAsArray);
            break;

        case "newUser":
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    gui.addNewUser(messageAsArray[1]);
                }
            });
            break;

        case "line":
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    gui.drawLineOnGUI(messageAsArray[1], messageAsArray[2],
                            messageAsArray[3], messageAsArray[4],
                            messageAsArray[5], messageAsArray[6],
                            messageAsArray[7], messageAsArray[8],
                            messageAsArray[9]);
                }
            });
            break;

        case "fillWhite":
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    gui.fillWithWhite();
                }
            });
            break;

        case "removeUser":
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    gui.removeUser(messageAsArray[1]);
                }
            });
            break;
        }
    }

    /**
     * serverListener is a functor that will be used for spinning a new Thread
     * listening for messages sent from the server over the socket.
     */
    private class serverListener implements Runnable {
        @Override
        public void run() {
            // Have a loop constantly checking for new messages from the
            // server.
            try {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    handleMessage(inputLine);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    // Close connection with server if we get an error or the
                    // while loop stops
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}