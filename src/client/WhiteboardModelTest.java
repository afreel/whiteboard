package client;

import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

import static org.junit.Assert.*;

import org.junit.Test;

public class WhiteboardModelTest {
    private List<String> clientSentMessages = new ArrayList<String>();
    //private List<String> clientReceivedMessages;
    private List<String> serverSentMessages = new ArrayList<String>();
    private List<String> serverReceivedMessages = new ArrayList<String>();
    
    @Test
    public void serverGetsMessage() throws IOException {
        String message1 = "HI";
        String message2 = "HI again";
        
        TopButtonBar topbar = new TopButtonBar();
        BottomButtonBar bottombar = new BottomButtonBar();
        UsersBar usersbar = new UsersBar(new ArrayList<String>());
        
        WhiteboardGUI gui = new WhiteboardGUI(topbar, bottombar, usersbar, 100, 100, "localhost", 1234);
        
        Thread dummyServer = new Thread(new echoServer());
        dummyServer.start();

        WhiteboardModel whitemodel = new WhiteboardModel("localhost",
                4444, gui);
        
        clientSentMessages.add(message1);
        whitemodel.sendMessageToServer(message1);
        
        clientSentMessages.add(message2);
        whitemodel.sendMessageToServer(message2);
        
        try {
            // Give the server Thread some time to process all the messages
            Thread.sleep(10);
            assertEquals(clientSentMessages, serverReceivedMessages);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates a server that prints out whichever message it gets and echoes
     * the same message back.
     */
    public class echoServer implements Runnable {
        @Override
        public void run() {
            int portNumber = 4444;
            PrintWriter out;
            BufferedReader in;
            
            try {
                @SuppressWarnings("resource")
                ServerSocket serverSocket = new ServerSocket(portNumber);
                System.out.println("Server started");
                
                Socket clientSocket = serverSocket.accept();
                String client = clientSocket.getInetAddress().toString();
                
                System.out.println(client
                        + " connected");

                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(
                        clientSocket.getInputStream()));

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    serverReceivedMessages.add(inputLine);
                    serverSentMessages.add("Server echoing '" + inputLine
                            + "' back to client");
                    // Echo the message back
                    out.println(inputLine);
                }

            } catch (IOException e) {
                System.out
                        .println("Exception caught when trying to listen on port "
                                + portNumber + " or listening for a connection");
                System.out.println(e.getMessage());
            }
        }
    }
}
