package server;

import java.util.ArrayList;
import java.util.List;

/**
 * Whiteboard represents a whiteboard on the server.  We will have an instance of this class for every whiteboard we have on
 * the server so that we may maintain an updated copy of every whiteboard whether there are clients connected to it or not.
 * When clients interact with this whiteboard and change it (connecting/disconnnecting/drawing/erasing), this whiteboard will
 * be updated to reflect the changes and stored in a list of whiteboards on the server side.
 * 
 */

/*
 * TESTING:
 * all public methods are run through base tests in WhiteboardTest.java
 * To more fully test Whiteboard.java, we tested manually as documented in docs/Manual Testing.txt
 *  
 */

/* Rep invariant: 
 *  - history != null
 *  - history.size() < Integer.MAX_VALUE. In a reasonable use of this Whiteboard class, this size will not be reached as only one item is 
 *    added to history every time a user draws a line.
 *  - clients != null
 */

/* ---------------------------------------------------Thread safety argument--------------------------------------------------------------/
 * Methods which mutate clients (addClient; removeClient) or interact with the clients in clients (sendMessageToAll) must acquire a lock
 * on clients. Additionally, usersMessage is only called within addClient, which must acquire a lock on clients. 
 * This monitor pattern prevents any possible race conditions on our clients list. 
 * 
 * The only methods accessing history are the mutator addLine and the observer loadWhiteboard. Both of these must acquire a lock on 
 * history, thus preventing any possible race conditions.
 *   
 * Whiteboard's fields are private, final, and accessed only by methods within this class. Thus, according to the above assertions, 
 * Whiteboard is threadsafe.
 */

public class Whiteboard {
	
	private final List<Client> clients; //list of clients currently using this whiteboard
	private final List<String> history; //list of line draw messages sent to this whiteboard since its creation
	
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

            this.sendMessageToAll("newUser "+client.getUsername());
            
    		clients.add(client);
    		client.sendMessage(this.usersMessage());
    		this.loadWhiteboard(client);
    	}
    }
    
    /**
     * Removes a client from the whiteboard and informs all clients connected to this whiteboard of the removal.
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
     * Message needs to be a line message.
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
    	synchronized (history) {
    		for (String message : history) {
    			client.sendMessage(message);
    		}
    	}
    }
    
    /**
     * Ensure our representation invariant is maintained
     */
    public void checkRep() {
    	assert(history != null);
    	assert(history.size() < Integer.MAX_VALUE -1);
    	assert(clients !=  null);
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
