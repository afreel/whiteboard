package client;

import java.net.*;
import java.io.*;

import static org.junit.Assert.*;

import org.junit.Test;

public class WhiteboardModelTest {

    @Test
    public void serverGetsMessage() throws IOException {
        
        Thread server = new Thread(new Runnable() {
            public void run() {
                int portNumber = 4444;
                PrintWriter out;
                BufferedReader in;
                               
                try {
                    @SuppressWarnings("resource")
                    ServerSocket serverSocket = new ServerSocket(portNumber);
                    Socket clientSocket = serverSocket.accept();
                    
                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    in = new BufferedReader(new InputStreamReader(
                            clientSocket.getInputStream()));
                    
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        // Echo the message back
                        System.out.println("Recieved message from client: '"+inputLine+"'");
                        out.println(inputLine);
                    }
                    
                } catch (IOException e) {
                    System.out
                    .println("Exception caught when trying to listen on port "
                            + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
                }

            }
        });
        
        Thread client = new Thread(new Runnable(){
            @Override
            public void run() {
                WhiteboardModel whitemodel = new WhiteboardModel("localhost",4444);
                whitemodel.sendMessageToServer("HI");
            }
        });
        
        server.start();
        client.start();
    }
}
