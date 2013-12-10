package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
	private String username;
	private PrintWriter printWriter;
	
	/**
	 * 
	 * @param username user's chosen username
	 * @throws IOException if error occurs while retrieving user's output stream
	 */
	public Client(String username, Socket socket) throws IOException {
		this.username = username;
		this.printWriter = new PrintWriter(socket.getOutputStream(), true);
	}
	
	/**
	 * Observer method for retrieval of this client's username
	 * @return this client's chosen username
	 */
	public String getUsername() {
		return this.username;
	}
	
	/**
	 * Sends a given message to this client.
	 * @param message message to be sent
	 */
	public void sendMessage(String message) {
		printWriter.println(message);
	}
}
