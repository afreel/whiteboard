package server;

import java.util.HashMap;

public class WhiteboardServer {

	private final HashMap<Integer, Whiteboard> whiteboardMap;
	
	public WhiteboardServer() {
		this.whiteboardMap = new HashMap<Integer, Whiteboard>() {{ put(1, new Whiteboard()); put(2, new Whiteboard()); }}; 
	}
	public static void main(String[] args) {
		
	}
}

