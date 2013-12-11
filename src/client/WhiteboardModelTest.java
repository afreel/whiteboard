package client;

import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

import static org.junit.Assert.*;

import org.junit.Test;
import testing.TestUtils;

public class WhiteboardModelTest {
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
     * STRATEGY:
     * 
     * We will test the client side from two ends:
     * 
     * - Check that the model can SEND messages to the server, and that the
     * correct messages are generated.
     * 
     * - Check that the model will RECEIVE message correctly, i.e. that the
     * Front End executes coherent methods when a message is generated by the
     * server.
     */

   
    @Test
    public void clientCorretlySendsDrawLineMessage() throws IOException {
        int portNumber = 4444;
        TestUtils test = new TestUtils();
        
        // Start up a dummy server for all tests
        test.startServer(portNumber);
        System.out.println("Server started");
        
        // Generate a dummy front end we can associate with the model
        WhiteboardModel modelToTest = new WhiteboardModel(new dummyFrontEnd());
        
        modelToTest.connectToServer("localhost", portNumber);

        // Send a message to the server
        modelToTest.drawLineOnServer(100, 100, 200, 200, 5, 0, 0, 0);
        clientSentMessages.add("line 100 100 200 200 5 0 0 0");

        try {
            // Give the server Thread some time to process the message
            Thread.sleep(100);
            assertEquals(clientSentMessages, serverReceivedMessages);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void clientCorrectlySendsUserMessage() throws IOException {
        // Start up a dummy server for all tests
        new Thread(new dummyServer(4445)).start();
        
        // Generate a dummy front end we can associate with the model
        WhiteboardModel modelToTest = new WhiteboardModel(new dummyFrontEnd());
        modelToTest.connectToServer("localhost", 4445);

        // Send a message to the server
        modelToTest.drawLineOnServer(100, 100, 200, 200, 5, 0, 0, 0);
        clientSentMessages.add("line 100 100 200 200 5 0 0 0");
        
        
        try {
            // Give the server Thread some time to process the message
            Thread.sleep(10);
            System.out.println(serverReceivedMessages);
            long endTime   = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            System.out.println(totalTime);
            
            assertEquals(clientSentMessages, serverReceivedMessages);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates a server, that stores whichever message it gets, at port 4444
     * on localhost.
     */
    public class dummyServer implements Runnable {
        private int portNumber;
        
        public dummyServer(int portNumber){
            this.portNumber = portNumber;
        }
        
        @Override
        public void run() {
            try {
                ServerSocket serverSocket = new ServerSocket(portNumber);
                
                while(true) {
                    final Socket clientSocket = serverSocket.accept();
                    
                    new Thread(new Runnable(){
                        public void run(){
                            BufferedReader in = null;
                            try {
                                in = new BufferedReader(new InputStreamReader(
                                        clientSocket.getInputStream()));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            String inputLine;
                            try {
                                while ((inputLine = in.readLine()) != null) {
                                    serverReceivedMessages.add(inputLine);
                                    long endTime   = System.currentTimeMillis();
                                    long totalTime = endTime - startTime;
                                    System.out.println(serverReceivedMessages);
                                    System.out.println("Received "+inputLine+" @ "+ totalTime);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            return;
                        }
                    }).start();
                    
                }

            } catch (IOException e) {
                System.out
                        .println("Exception caught when trying to listen on port "
                                + portNumber + " or listening for a connection");
                System.out.println(e.getMessage());
            }
        }
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

        @Override
        public void loadUsernameTakenImage() {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void loadConnectedToServerImage() {
            // TODO Auto-generated method stub
            
        }
    }

}
