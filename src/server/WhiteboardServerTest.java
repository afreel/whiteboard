package server;

import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

import static org.junit.Assert.*;

import org.junit.Test;

public class WhiteboardServerTest {
    String host = "localhost";
    int port = 4444;
    
    @Test
    public void clientTest() {
        final WhiteboardServer server = new WhiteboardServer();
        
        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    server.serve(port);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        
        dummyClient client1 = new dummyClient("james");
        
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("james: sends line message");
        client1.sendMessage("line 100 100 200 200 10 0 0 0");
        client1.sendMessage("line 50 50 200 200 10 0 0 0");
        
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        dummyClient client2 = new dummyClient("steven");
    }

    public class dummyClient {
        Socket socket;
        PrintWriter out;
        BufferedReader in;
        
        public dummyClient(final String username) {
            String whiteboard = "1";

            System.out.println(username+": created.");

            try {
                socket = new Socket(host, port);
                System.out.println(username + ": socket connection established");
                // Out, accessed to send data to the server:
                out = new PrintWriter(socket.getOutputStream(), true);
                // In, accessed to get data from the server, need a while loop to
                // get check if it is ever updated:
                in = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));
                
                // Tell the server we want to connect to a specific whiteboard
                out.println("whiteboard " + whiteboard + " username "
                        + username);
                
            } catch (IOException e1) {
                //
                System.out.println(username+": Couldnt connect to " + host + " @ port "
                        + port);
                e1.printStackTrace();
            }

            // Have a thread constantly listen for server messages
            new Thread(new Runnable() {
                public void run() {
                    // Have a loop constantly checking for new messages from the
                    // server.
                    try {
                        String inputLine;
                        while ((inputLine = in.readLine()) != null) {
                            System.out
                                    .println(username + ": Recieved message '"
                                            + inputLine + "'");
                        }
                    } catch (IOException e) {
                        e.printStackTrace(); // but don't terminate server
                    } finally {
                        try {
                            // Close connection with server
                            System.out
                                    .println(username + ": Closing connection with server");
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }
        
        public void sendMessage(String message){
            out.println(message);
        }
    }

}
