package client;

import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

import static org.junit.Assert.*;

import org.junit.Test;

public class WhiteboardModelTest {
    private List<String> clientSentMessages = new ArrayList<String>();
    private List<String> serverReceivedMessages = new ArrayList<String>();
    
    @Test
    public void serverGetsMessage() throws IOException {
        // This is the message we will try to send to the server
        String message1 = "test message 1";

        // Start up a dummy server
        new Thread(new listenServer()).start();
        
        // Generate a dummy GUI we can associate with the model
        WhiteboardGUI gui = guiBuilderHelper();
        WhiteboardModel whitemodel = gui.getModel();

        // Send a message to the server
        clientSentMessages.add(message1);
        whitemodel.sendMessageToServer(message1);
        
        try {
            // Give the server Thread some time to process the message
            Thread.sleep(10);
            assertEquals(clientSentMessages, serverReceivedMessages);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void serverGetsLineMessage() throws IOException {
        
        // This is the message we will try to send to the server
        String expect = "line 100 100 200 200 5 0 0 0";
        
        // Start up a dummy server
        new Thread(new listenServer()).start();
        
        // Generate a dummy GUI we can associate with the model
        WhiteboardGUI gui = guiBuilderHelper();
        WhiteboardModel whitemodel = gui.getModel();

        // Send a message to the server
        clientSentMessages.add(expect);
        whitemodel.drawLineOnServer(100, 100, 200, 200, 5, 0, 0, 0);

        try {
            // Give the server Thread some time to process the message
            Thread.sleep(10);
            assertEquals(clientSentMessages, serverReceivedMessages);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates a server that stores whichever message it gets.
     */
    public class listenServer implements Runnable {
        @Override
        public void run() {
            int portNumber = 4444;
            BufferedReader in;
            
            try {
                ServerSocket serverSocket = new ServerSocket(portNumber);
                Socket clientSocket = serverSocket.accept();

                in = new BufferedReader(new InputStreamReader(
                        clientSocket.getInputStream()));

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    serverReceivedMessages.add(inputLine);
                }
                return;

            } catch (IOException e) {
                System.out
                        .println("Exception caught when trying to listen on port "
                                + portNumber + " or listening for a connection");
                System.out.println(e.getMessage());
            }
        }
    }

    public WhiteboardGUI guiBuilderHelper() {
        TopButtonBar topbar = new TopButtonBar();
        BottomButtonBar bottombar = new BottomButtonBar();
        UsersBar usersbar = new UsersBar(new ArrayList<String>());
        return new WhiteboardGUI(topbar, bottombar, usersbar, 100, 100,
                "localhost", 4444);
    }
    
    
}
