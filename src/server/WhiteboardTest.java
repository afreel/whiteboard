package server;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.Socket;
import testing.TestUtils;
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
		try {
			TestUtils.startServer();
			Whiteboard board = new Whiteboard();
			Client c = new Client("Test", TestUtils.spawnClient("Test", "1"));
			board.addClient(c);
			assertEquals(board.getClientsTesting().get(0), c);
			TestUtils.killServer();
		}
		catch (IOException e1) {}
	}
	
	@Test
	public void addClonedClientTest() {
		try {
			TestUtils.startServer();
			Whiteboard board = new Whiteboard();
			Client c1 = new Client("Joe", TestUtils.spawnClient("Test", "1"));
			Client c2 = new Client("Joe", TestUtils.spawnClient("Test", "1"));
			board.addClient(c1);
			board.addClient(c2);
			assertEquals(board.getClientsTesting().get(0), c1);
			assertEquals(board.getClientsTesting().get(1), c2);
			TestUtils.killServer();
		} catch (IOException e) {}
	}
	
	@Test
	public void removeClientBasicTest() {
		try {
			TestUtils.startServer();
			Whiteboard board = new Whiteboard();
			Client c1 = new Client("Mike", TestUtils.spawnClient("Test", "1"));
			Client c2 = new Client("Austin", TestUtils.spawnClient("Test", "1"));
			board.addClient(c1);
			board.addClient(c2);
			board.removeClient(c1);
			assertEquals(board.getClientsTesting().size(), 1);
			assertEquals(board.getClientsTesting().get(0), c2);
			TestUtils.killServer();
		} catch (IOException e) {}
	}
	
	@Test
	public void removeNonexistentClientTest() {
		try {
			TestUtils.startServer();
			Whiteboard board = new Whiteboard();
			Client c1 = new Client("Johannes", TestUtils.spawnClient("Test", "1"));
			Client c2 = new Client("Joe", TestUtils.spawnClient("Test", "1"));
			board.addClient(c1);
			board.removeClient(c2);
			assertEquals(board.getClientsTesting().size(), 1);
			assertEquals(board.getClientsTesting().get(0), c1);
			TestUtils.killServer();
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
