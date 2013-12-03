package canvas;

import java.io.*;
import java.net.*;
import java.util.*;

public class WhiteboardServer {
	private final ServerSocket serverSocket;
	
	
	/**
     * Make a WhitebaordServer that listens for connections on port.
     * 
     * @param port port number, requires 0 <= port <= 65535
     */
	public WhiteboardServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }
	
	/**
     * Run the server, listening for client connections and handling them.
     * Never returns unless an exception is thrown.
     * 
     * @throws IOException if the main server socket is broken
     *                     (IOExceptions from individual clients do *not* terminate serve())
     */
    public void serve() throws IOException {
        while (true) {
            // block until a client connects
            final Socket socket = serverSocket.accept();
            
            Thread thread = new Thread(new Runnable() {
            	public void run() {
            		// handle the client
                    try {
                        handleConnection(socket);
                    } catch (IOException e) {
                        e.printStackTrace(); // but don't terminate serve()
                    } finally {
                        try {
							socket.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                    }
            	}
            });
            //start thread
            thread.start();

        }
    }

    /**
     * Handle a single client connection. Returns when client disconnects.
     * 
     * @param socket socket where the client is connected
     * @throws IOException if connection has an error or terminates unexpectedly
     */
    private void handleConnection(Socket socket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        
        try {
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                broadcastLine(line);          
            }
        } finally {
            out.close();
            in.close();
        }
    }
    
    private void broadcastLine(String line) {
    	
    }
}
