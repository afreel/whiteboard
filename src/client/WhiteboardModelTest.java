package client;

import java.net.*;
import java.util.List;
import java.io.*;

import static org.junit.Assert.*;

import org.junit.Test;

public class WhiteboardModelTest {
    private List<String> clientSentMessages;
    private List<String> clientReceivedMessages;
    private List<String> serverSentMessages;
    private List<String> serverReceivedMessages;

    @Test
    public void serverGetsMessage() throws IOException {
        String message1 = "HI";
        String message2 = "HI again";
        
        new Thread(new echoServer()).start();

        WhiteboardModel whitemodel = new WhiteboardModel("localhost",
                4444);
        
        clientSentMessages.add(message1);
        whitemodel.sendMessageToServer(message1);
        
        clientSentMessages.add(message2);
        whitemodel.sendMessageToServer(message2);

        try {
            // We need to make the algorithm wait so we can visually check that
            // the GUI is correct
            Thread.sleep(500);
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
                    serverReceivedMessages.add("Server recieved: '" + inputLine + "'");
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
