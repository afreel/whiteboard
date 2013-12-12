package server;

import java.io.*;
import java.net.UnknownHostException;

import static org.junit.Assert.*;

import org.junit.Test;

import testing.TestUtils;

public class WhiteboardServerTest {
    String host = "localhost";
    int port = 4444;
    
    @Test
    public void clientTest() throws UnknownHostException, IOException {
        final WhiteboardServer server = new WhiteboardServer();
        TestUtils utils = new TestUtils();
        final int port = 4500;
        
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
        
    }


}
