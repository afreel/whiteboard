package testing;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

import client.WhiteboardFrontEnd;
/*
 * TESTING
 * 
 * NOTES:
 * 
 * The testing was implemented so that it would be independent from any
 * other existing class and/or these other classes functionality. It only
 * depends on WhiteboardFrontEnd interface.
 * 
 * Almost all methods in WhiteboardModel rely on having a server to which we
 * will send messages. We therefore have implemented a runnable called
 * dummyServer which starts a server and adds any received messages to a
 * field that all threads can share: serverRecievedMessages. Since we only
 * have one server at a time we don't risk any concurrency with this shared
 * memory.
 * 
 * WhiteboardModel also needs a WhiteboardFrontEnd when instantiated. This
 * is why we wrote the WhiteboardFrontEnd interface, so that the model could
 * be attached to any class having the given methods specified in the
 * interface(not locking it to WhiteboardGUI). This also makes testing much
 * easier in the sense that we can automatically test whether the model is
 * calling the correct methods in the gui.
 * 
 */
public class TestUtils {
    private Socket clientSocket;
    private PrintWriter socketOut;
    public List<String> serverReceivedMessages = new ArrayList<String>();
    public ArrayList<ArrayList<String>> clientReceivedMessages = new ArrayList<ArrayList<String>>();
    public List<String> guiReceivedMessages = new ArrayList<String>();
    

    /**
     * Generates a server
     * 
     * @throws IOException
     */
    public void startServer(final int portNo) throws IOException {
        new Thread(new Runnable() {
            public void run() {
                ServerSocket serverSocket = null;

                try {
                    serverSocket = new ServerSocket(portNo);
                    
                    while(true){
                        clientSocket = serverSocket.accept();
                        socketOut = new PrintWriter(clientSocket.getOutputStream(),
                                true);
                        
                        new Thread(new serverListener()).start();
                    }
                    
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }
        }).start();
    }

    public void serverSendToClient(String message) {
        socketOut.println(message);
    }
    

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
     * dummyFrontEnd allows us to call the model's constructor without needing the class WhiteboardGUI.
     */
    public class dummyFrontEnd implements WhiteboardFrontEnd {
        
        public void drawLineOnGUI(String strx1, String stry1, String strx2,
                String stry2, String strwidth, String strr, String strg,
                String strb, String user) {
            guiReceivedMessages.add("line "+strx1 + " " + stry1 + " " + strx2 + " "
                    + stry2 + " " + strwidth + " " + strr + " " + strg + " "
                    + strb + " " + user);
        }

        public void fillWithWhite() {
            
        }

        public void addNewUser(String user) {
            guiReceivedMessages.add("newUser "+user);
        }

        public void removeUser(String user) {
            guiReceivedMessages.add("users "+user);
        }

        public void loadGuiUsers(List<String> usersList) {
            String string = "";
            for(String user: usersList){
                string += " "+user;
            }
            guiReceivedMessages.add("users"+string);
        }

        public void loadUsernameTakenImage() {
        }

        public void loadConnectedToServerImage() {
        }
    }

    public void sleep() {
        try {
            // Give the server Thread some time to process the message
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
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
