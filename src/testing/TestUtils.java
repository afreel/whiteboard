package testing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class TestUtils {
    private static final int portNumber = 4444;
    private static boolean runserver = true;
    private static boolean runclients = true;
    private static Socket clientSocket;
    private static List<PrintWriter> listOfPrinters = new ArrayList<PrintWriter>();
    private static List<String> serverReceivedMessages = new ArrayList<String>();
    private static final long startTime = System.currentTimeMillis();

    /**
     * Generates a server
     * 
     * @throws IOException
     */
    @SuppressWarnings("resource")
    public static void startServer() throws IOException {
        new Thread(new Runnable() {
            public void run() {
                ServerSocket serverSocket = null;

                try {
                    serverSocket = new ServerSocket(portNumber);
                    System.out.println("Started server");
                    clientSocket = serverSocket.accept();
                    System.out.println("Accepted a client");

                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                new Thread(new Runnable() {
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
                            while ((inputLine = in.readLine()) != null
                                    && runserver) {
                                serverReceivedMessages.add(inputLine);
                                long endTime = System.currentTimeMillis();
                                long totalTime = endTime - startTime;
                                System.out.println(serverReceivedMessages);
                                System.out.println("Received " + inputLine
                                        + " @ " + totalTime);
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
                }).start();
            }
        }).start();
    }

    public static void killServer() {
        runserver = false;
        
     // We need the server to get a readline() before it will know it has to exit the while loop 
     // The message we send could be anything(even empty!)
        for(PrintWriter printer: listOfPrinters){
            printer.println("goodbye");
        }
    }

    public static PrintWriter spawnClient(final String username, String whiteboard)
            throws UnknownHostException, IOException {
        String host = "localhost";

        // Connect to the socket
        System.out.println("attempting to connect to socket");
        final Socket socket = new Socket(host, portNumber);
        System.out.println(username + ": socket connection established");

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
                    while ((inputLine = in.readLine()) != null && runclients && socket.isConnected()) {
                        System.out.println(username + ": Recieved message '"
                                + inputLine + "'");
                    }
                } catch (IOException e) {
                    e.printStackTrace(); // but don't terminate server
                } finally {
                    try {
                        // Close connection with server
                        System.out.println(username
                                + ": Closing connection with server");
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        
        listOfPrinters.add(out);
        return out;
    }

    
    public static void main(String[] args) throws IOException {
        startServer();
        PrintWriter pw = spawnClient("user1", "1");
        pw.println("hi there server");
        sleep();
        killServer();
    }
    
    private static void sleep(){
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
