package client;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

/**
 * WhiteboardModel is the back-end of the Whiteboard on the client side. It is
 * responsible for connecting the client with the server and listening for
 * server messages, and execute the expected behavior from the given message.
 * 
 * TESTING: disconnectFromServer() was tested manually by: > connecting to an
 * instance of WhiteboardServer from another computer > closing client window
 * and ensuring that the correct message was sent by using a print statement >
 * ensuring that client was removed from usersbar of those connected > ensuring
 * that a new user could use that disconnected user's username
 * 
 */

/*
 * Rep. Invariant: 
 * - socket != null. 
 * - usersList always contains the username of this client as long as
 *   its connected to a whiteboard
 */

/*-----------------------------------------------------------Thread-safety Argument-----------------------------------------------------//
 * Concurrency is introduced in this class by the necessity to listen for both requests from WhiteboardGUI, and messages sent from the server
 * This is implementing by spinning a new thread (of a ServerListener instance) on connection to a whiteboard which listens to messages from the server. Actions on 
 * the WhiteboardGUI will call methods in this model, but those calls will be made on the GUI's main thread. This allows concurrent usage of 
 * a WhiteboardModel instance.
 * 
 * Calls on methods in WhiteboardGUI from this class are all made using SwingUtilities.invokeLater, in handleMessage. This ensures that events which modify
 * the GUI are placed on the AWT EventQueue, protecting our GUI rep from the inherently multi-threaded nature of Swing.
 * 
 * As mentioned above, relevant changes to WhiteboardGUI which call methods in this class and send messages out to the server are handled
 * on a completely parallel thread to the one listening for server messages. Thus, given the thread-safeness of WhiteboardServer, actions
 * through this model maintain the thread-safety of this datatype.
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
     *             if can't acces the output or input stream of the server or
     *             can't connect to the server
     */
    public void connectToServer(String host, int port) throws IOException {
        // Connecting to the server
        socket = new Socket(host, port);

        // Store the output stream of the socket, accessed to send messages to
        // the server:
        out = new PrintWriter(socket.getOutputStream(), true);

        // Store the input stream of the server
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

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

    /**
     * Tells the server that the client is disconnecting
     */
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
     * @param usernameConfirmed
     *            a boolean indicating whether or not it is necessary to listen
     *            for a server response here. If a client's username has already
     *            been confirmed as unique, then we need not listen for a server
     *            response and may return true.
     * @throws IOException
     *             if an IO error occurs on retrieving the BufferedReader
     *             instance
     * @returns true if the client's submitted username is accepted as unique,
     *          or has already been accepted as so. false if another user is
     *          connected to the server with the same username.
     */
    public boolean connectToWhiteBoard(String whiteboard, String username,
            boolean usernameConfirmed) throws IOException {

        sendMessageToServer("whiteboard " + whiteboard + " username "
                + username);

        // If the client's user name has already been accepted, then we don't
        // want to start a new server listener, else we would be doubling up the
        // messages sent to the client
        if (usernameConfirmed) {
            return true;
        } else {
            BufferedReader inReader = new BufferedReader(in);
            String inputLine = inReader.readLine();
            while (inputLine == null) {
                inputLine = inReader.readLine();
            }
            ;

            if (inputLine.equals("usernameTaken")) {
                gui.loadUsernameTakenImage();
                return false;
            } else {
                handleMessage(inputLine);
                new Thread(new ServerListener()).start(); // Have a thread
                                                          // constantly listen
                                                          // for server messages
            }
            return true;
        }
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
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    gui.drawLineOnGUI(messageAsArray[1], messageAsArray[2],
                            messageAsArray[3], messageAsArray[4],
                            messageAsArray[5], messageAsArray[6],
                            messageAsArray[7], messageAsArray[8],
                            messageAsArray[9]);
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
    private class ServerListener implements Runnable {
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
    /**
     * Ensure our representation invariant is maintained
     */
    public void checkRep() {
    	assert(usersList != null);
    }

}