package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class WhiteboardModel {
    private final Socket socket;
    
	WhiteboardModel(String host, int port) throws UnknownHostException, IOException{
    socket = new Socket(host, port);
    PrintWriter out =
        new PrintWriter(socket.getOutputStream(), true);
    BufferedReader in =
        new BufferedReader(
            new InputStreamReader(socket.getInputStream()));
    BufferedReader stdIn =
        new BufferedReader(
            new InputStreamReader(System.in));
        
	    try {
	            String userInput;
	            while ((userInput = stdIn.readLine()) != null) {
	                out.println(userInput);
	                System.out.println("echo: " + in.readLine());
	            }
	        } catch (UnknownHostException e) {
	            System.err.println("Don't know about host " + host);
	            System.exit(1);
	        } catch (IOException e) {
	            System.err.println("Couldn't get I/O for the connection to " +
	                host);
	            System.exit(1);
	        } 
	}
}
