package canvas;

import java.net.Socket;

public class Client {
	private String username;
	private Socket socket;
	
	public Client(String username, Socket socket) {
		this.username = username;
		this.socket = socket;
	}
	
	public Socket getSocket() {
		return this.socket;
	}
	
	public String getUsername() {
		return this.username;
	}
}
