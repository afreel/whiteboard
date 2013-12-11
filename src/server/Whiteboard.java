package server;

import java.util.ArrayList;
import java.util.List;

/**
 * Whiteboard represents a whiteboard on the server.  We will have an instance of this class for every whiteboard we have on
 * the server so that we may maintain an updated copy of every whiteboard whether there are clients connected to it or not.
 * When clients interact with this whiteboard and change it (connecting/disconnnecting/drawing/erasing), this whiteboard will
 * be updated to reflect the changes and stored in a list of whiteboards on the server side.
 */

public class Whiteboard {
	
	private List<Client> clients; //list of clients currently using this whiteboard
	private List<String> history; //list of line draw messages sent to this whiteboard since its creation
	
	/**
     * Make a whiteboard.
     */
    public Whiteboard() {
    	clients = new ArrayList<Client>();
    	history = new ArrayList<String>();
    }
    
    /**
     * Adds a client to the whiteboard and informs all clients connected to this whiteboard of the addition.
     * @param client client to be added to this whiteboard
     */
    public void addClient(Client client) {
    	synchronized (clients) {
    		this.sendMessageToAll("newUser " + client.getUsername());
    		clients.add(client);
    		System.out.println("--> Sending list of current users to client");
    		client.sendMessage(this.usersMessage());
    		System.out.println("--> Sending history to client");
    		this.loadWhiteboard(client);
    	}
    }
    
    /**
     * Remove a client from the whiteboard and informs all clients connected to this whiteboard of the removal.
     * @param client client to be removed from this whiteboard
     */
    public void removeClient(Client client) {
    	synchronized (clients) {
    		clients.remove(client);
    		this.sendMessageToAll("removeUser " + client.getUsername());
    	}
    }
    
    /**
     * 
     * @return a String of the form "users a b c ... n", where a, b, c, ... , n are the current clients
     * interacting with this whiteboard
     */
    private String usersMessage() {
    	StringBuilder message = new StringBuilder();
		message.append("users");
		for (Client c : this.clients) {
			message.append(" " + c.getUsername());
		}
		return message.toString();
    }
    
    /**
     * Sends a String message to all current clients interacting with this whiteboard.
     * @param message message to send
     */
    private void sendMessageToAll(String message) {
    	synchronized (clients) {
    		for (Client client : this.clients) {
        		client.sendMessage(message);
        	}
    	}
    }
    
    /**
     * Adds a line to this whiteboard's history list and sends this line to all users connected to this whiteboard.
     * @param message String message encoding the line being added to this whiteboard
     */
    public void addLine(String message) {
    	synchronized (history) {
    		history.add(message);
    	}
    	this.sendMessageToAll(message);
    }
    
    /**
     * Sends the list of lines drawn on this board to a client.
     * Note: this is used to allow a new client connecting to this board to have full access to the board's history.
     * @param client client to send history to
     */

    private void loadWhiteboard(Client client) {
    	System.out.println("sending Hisotry");
    	synchronized (history) {
    		for (String message : history) {
    			client.sendMessage(message);
    		}
    	}
    }
    
    //METHODS BELOW ARE ONLY USED FOR TESTING
    
    /**
     * TESTING
     * @return list of whiteboard's clients
     */
    public List<Client> getClientsTesting() {
    	return this.clients;
    }
    
    /**
     * TESTING
     * @return list of whiteboard's line message history
     */
    public List<String> getHistoryTesting() {
    	return this.history;
    }
}
