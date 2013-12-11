package server;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.Socket;

import org.junit.Test;

/*TESTING STRATEGY:
 * 
 * addClient(Client c): 
 * > ensure client can be properly added
 * > test case when clone of Client c already exists in clients list
 * 
 * removeClient(Client c):
 * > ensure client is removed from the clients list
 * > test case when Client c is not in the clients list
 * 
 * addLine(String message):
 * > check that line is added to history list
 * > test case when message is empty
 * > test case when message is abstract format
 * 
 */

public class WhiteboardTest {

	@Test
	public void addClientBasicTest() {
		Whiteboard board = new Whiteboard();
		try {
			Client c = new Client("Test", new Socket());
			board.addClient(c);
			assertEquals(board.getClientsTesting().get(0), c);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void addClonedClientTest() {
		Whiteboard board = new Whiteboard();
		try {
			Client c1 = new Client("Joe", new Socket());
			Client c2 = new Client("Joe", new Socket());
			board.addClient(c1);
			board.addClient(c2);
			assertEquals(board.getClientsTesting().get(0), c1);
			assertEquals(board.getClientsTesting().get(1), c2);
		} catch (IOException e) {}
	}
	
	@Test
	public void removeClientBasicTest() {
		Whiteboard board = new Whiteboard();
		try {
			Client c1 = new Client("Mike", new Socket());
			Client c2 = new Client("Austin", new Socket());
			board.addClient(c1);
			board.addClient(c2);
			board.removeClient(c1);
			assertEquals(board.getClientsTesting().size(), 1);
			assertEquals(board.getClientsTesting().get(0), c2);
		} catch (IOException e) {}
	}
	
	@Test
	public void removeNonexistentClientTest() {
		Whiteboard board = new Whiteboard();
		try {
			Client c1 = new Client("Mike", new Socket());
			Client c2 = new Client("Austin", new Socket());
			board.addClient(c1);
			board.removeClient(c2);
			assertEquals(board.getClientsTesting().size(), 2);
			assertEquals(board.getClientsTesting().get(0), c1);
		} catch (IOException e) {}
	}
	
	@Test
	public void addLineBasicTest() {
		Whiteboard board = new Whiteboard();
		board.addLine("hello");
		assertEquals(board.getHistoryTesting().get(0), "hello");
	}
	
	@Test
	public void addEmptyLineTest() {
		Whiteboard board = new Whiteboard();
		board.addLine("");
		assertEquals(board.getHistoryTesting().size(), 1);
	}
	
	@Test
	public void addAbstractLineTest() {
		Whiteboard board = new Whiteboard();
		board.addLine("!@#$%^&*");
		assertEquals(board.getHistoryTesting().get(0), "!@#$%^&*");
	}

}
