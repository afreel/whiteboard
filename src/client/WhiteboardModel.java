package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
    
    public class WhiteboardModel {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private WhiteboardGUI gui;
        private List<String> usersList = new ArrayList<String>();
        
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
       public WhiteboardModel(WhiteboardGUI associatedGUI) {          
    	   gui = associatedGUI;
        }

        public void sendMessageToServer(String message) {
            out.println(message);
        }

        public void connectToWhiteBoard(String whiteboard, String username) {
            out.println("whiteboard " + whiteboard + " username " + username);
        }

        public void drawLineOnServer(int x1, int y1, int x2, int y2, int width,
                int r, int g, int b) {
        	out.println("line " + Integer.toString(x1) + " " + Integer.toString(y1)
                    + " " + Integer.toString(x2) + " " + Integer.toString(y2) + " "
                    + Integer.toString(width) + " " + Integer.toString(r) + " "
                    + Integer.toString(g) + " " + Integer.toString(b));        	
        }
        
        public void connectToServer(String host, int port) {
        	try {
            	// Instantiate all finals
                socket = new Socket(host, port);
                System.out.println("Waiting..");
                System.out.println("Waiting...");
                // Out, accessed to send data to the server:
                out = new PrintWriter(socket.getOutputStream(), true);
                // In, accessed to get data from the server, need a for loop to get
                // check if it is ever updated:
                in = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));
            } catch (IOException e1) {
                System.out
                        .println("Couldnt connect to " + host + " @ port " + port);
                e1.printStackTrace();
            }

            // Have a thread constantly listen for server messages
            new Thread(new serverListener()).start();
        }
        
        public void disconnectFromServer() {
        	out.println("disconnect");
        }
        
        private void updateUsersList(String[] usersArray) {
        	usersList = new ArrayList<String>();
        	for (int i = 1; i < usersArray.length; i++) {
				usersList.add(usersArray[i]);
				}
        	gui.updateGuiUsers(usersList);
        }
        
        
        private void handleMessage(String message) {
        	final String[] messageAsArray = message.split(" ");
        	switch (messageAsArray[0]) {
        	case "line": javax.swing.SwingUtilities.invokeLater(new Runnable() {
        		public void run() {
        			gui.drawLineOnGUI(messageAsArray[1], messageAsArray[2], messageAsArray[3], messageAsArray[4], messageAsArray[5], messageAsArray[6], messageAsArray[7], messageAsArray[8]);
        		}
        	}); break;
        	case "users": updateUsersList(messageAsArray); break;
        	
        	case "fillWhite": javax.swing.SwingUtilities.invokeLater(new Runnable() {
        		public void run() {
        			gui.fillWithWhite();
        		}
        	}); break;
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
                        System.out.println("Client just got from the server: '"
                                + inputLine + "'");
                        handleMessage(inputLine);
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
        }

  }