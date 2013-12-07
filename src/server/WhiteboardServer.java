package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import client.WhiteboardGUI;

/**
 * 
 * Main server class. An instance of this class will listen on a specific port for new connecting clients.
 * For each connected client, it will spin a thread which waits for the client to select their whiteboard and username.
 * Once the client chooses a whiteboard and username, the client is associated with that whiteboard, and a new thread
 * is spun to listen for changes to they make to their local whiteboard.
 *
 */

public class WhiteboardServer {

	private final HashMap<String, Whiteboard> whiteboardMap;
	private final List<Thread> threadList;
	
	public WhiteboardServer() {
		this.whiteboardMap = new HashMap<String, Whiteboard>() {{ put("1", new Whiteboard()); put("2", new Whiteboard()); }}; 
		this.threadList = new ArrayList<Thread>();
	}
	
	/**
	 * This method will listen for any new connecting clients.
	 * New clients who successfully connect will be spun their own thread, which wait for them to chose their desired
	 * whiteboard and username. On receipt of these values, the thread will create a new Client object and add that 
	 * object to the clients list of the user's chosen whiteboard.
	 * 
	 * @param port the port this server will listen on
	 * @throws IOException if an error occurs when this tries to accept a new port
	 */
	@SuppressWarnings("resource")
	public void serve(int port) throws IOException {
		ServerSocket serverSocket = new ServerSocket(port);
		
		while(true) {
			final Socket socket = serverSocket.accept();
			
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					
					try {
						boolean connectedToWhiteboard = false;
						BufferedReader clientIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
						while (!connectedToWhiteboard) {
							String inputLine = clientIn.readLine();
							while (inputLine == null) { //Wait for the client to send a message
								inputLine = clientIn.readLine();
							}
							// Extract desired information from the client's message
							String[] inputAsArray = inputLine.split(" ");
							if (inputAsArray[0].equals("whiteboard")) {
								String chosenWhiteboard = inputAsArray[1];
								String userName = inputAsArray[3];
								Client newClient = new Client(userName, socket);
								System.out.println(chosenWhiteboard);
								System.out.println(newClient);
								whiteboardMap.get(chosenWhiteboard).addClient(newClient);
								
								Thread handleClient = new Thread(new ClientHandler(socket, chosenWhiteboard));
								handleClient.start();
								threadList.add(handleClient);
								connectedToWhiteboard = true;
							}
						}
		
					}
					catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			
		thread.start();
		}
	}

	/**
	 * 
	 * This will handle all messages sent from a given client.
	 *
	 */
	class ClientHandler implements Runnable {
		private final Socket socket;
		private final String whiteboardID;
		
		public ClientHandler(Socket socket, String whiteboardID) {
			this.socket = socket;
			this.whiteboardID = whiteboardID;
		}
		

		@Override
		public void run() {
			try {
				BufferedReader clientIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				
				String inputLine = clientIn.readLine();
				while(true){
					while (inputLine == null) {
						inputLine = clientIn.readLine();
					}
					System.out.println("Server Received message");
					whiteboardMap.get(whiteboardID).addLine(inputLine);
					inputLine = clientIn.readLine();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		final WhiteboardServer server = new WhiteboardServer();
		Thread thread = new Thread(new Runnable() {
			public void run() {
				try {
					server.serve(4444);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
		WhiteboardGUI.main(new String[]{});
//		WhiteboardGUI.main(new String[]{});
//		WhiteboardGUI.main(new String[]{});
//		WhiteboardGUI.main(new String[]{});

	}
	
}

