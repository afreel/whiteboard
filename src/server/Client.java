package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
	private final String username;
	private final PrintWriter printWriter;
	
	/*
	 * Rep invariant:
	 * - username != null
	 * - printWriter != null
	 */
	
	/**
	 * Construct a Client object
	 * @param username user's chosen username
	 * @throws IOException if error occurs while retrieving user's output stream
	 */
	public Client(String username, PrintWriter printWriter) throws IOException {
		this.username = username;
		this.printWriter = printWriter;
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
		System.out.println(message);
		printWriter.println(message);
	}
	
	/**
	 * Ensure that our representation invariant is maintained
	 */
	public void checkRep() {
		assert(username != null);
		assert(printWriter != null);
	}
	
}
