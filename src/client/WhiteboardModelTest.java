package client;

import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import testing.TestUtils;

public class WhiteboardModelTest {
    /*
     * TESTING STRATEGY:
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
    public void clientCorretlySendsDrawLineMessages() throws IOException {
        int portNumber = 4444;
        TestUtils test = new TestUtils();
        List clientSentMessages = new ArrayList<String>();
        
        test.startServer(portNumber);
        System.out.println("Server started");
        
        // Generate a dummy front end we can associate with the model
        WhiteboardModel modelToTest = new WhiteboardModel(test.new dummyFrontEnd());
        modelToTest.connectToServer("localhost", portNumber);

        // Send a message to the server
        modelToTest.drawLineOnServer(100, 100, 200, 200, 5, 0, 0, 0);
        clientSentMessages.add("line 100 100 200 200 5 0 0 0");

        test.sleep();
        assertEquals(clientSentMessages, test.serverReceivedMessages);
    }
    
    @Test
    public void clientCorretlySendsWhiteboardMessages() throws IOException {
        int portNumber = 4445;
        TestUtils test = new TestUtils();
        List clientSentMessages = new ArrayList<String>();
        
        test.startServer(portNumber);
        System.out.println("Server started");
        
        // Generate a dummy front end we can associate with the model
        WhiteboardModel modelToTest = new WhiteboardModel(test.new dummyFrontEnd());
        modelToTest.connectToServer("localhost", portNumber);

        // Send a message to the server
        modelToTest.connectToWhiteBoard("whiteboard", "username", true);
        clientSentMessages.add("whiteboard whiteboard username username");

        test.sleep();
        assertEquals(clientSentMessages, test.serverReceivedMessages);
    }
    
    @Test
    public void clientCorretlySendsDisconnectMessage() throws IOException {
        int portNumber = 4446;
        TestUtils test = new TestUtils();
        List clientSentMessages = new ArrayList<String>();
        
        test.startServer(portNumber);
        System.out.println("Server started");
        
        // Generate a dummy front end we can associate with the model
        WhiteboardModel modelToTest = new WhiteboardModel(test.new dummyFrontEnd());
        modelToTest.connectToServer("localhost", portNumber);
        
        // Send a message to the server
        modelToTest.disconnectFromServer();
        clientSentMessages.add("disconnect");

        test.sleep();
        assertEquals(clientSentMessages, test.serverReceivedMessages);
    }
    
    @Test
    public void clientCorretlyReceivesDrawLineMessage() throws IOException {
        int portNumber = 4447;
        TestUtils test = new TestUtils();
        List serverSentMessages = new ArrayList<String>();
        
        test.startServer(portNumber);
       
        // Generate a dummy front end we can associate with the model
        WhiteboardModel modelToTest = new WhiteboardModel(test.new dummyFrontEnd());
        modelToTest.connectToServer("localhost", portNumber);
        modelToTest.connectToWhiteBoard("whiteboard", "username", false);
        
        // Send a message to the client
        test.sleep(); // Make sure the server has been created by now
        System.out.println("Slept for some time");
        test.serverSendToClient("line 0 0 200 200 5 0 0 0 username");
        serverSentMessages.add("line 0 0 200 200 5 0 0 0 username");
        
        test.sleep();
        
        assertEquals(serverSentMessages, test.clientReceivedMessages);
    }
}
