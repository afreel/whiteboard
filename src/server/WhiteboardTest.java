package server;

import static org.junit.Assert.*;

import java.io.IOException;
import testing.TestUtils;
import org.junit.Test;

/*TESTING STRATEGY:
 * 
 * addClient(Client c): 
 * > ensure client is added to the clients list
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
    private String sampleLineMessage = "line 100 100 200 200 5 0 0 0";
    
	@Test
	public void addClientBasicTest() {
		try {
			TestUtils utils = new TestUtils();
			utils.startServer(4444);
			Whiteboard board = new Whiteboard();
			Client c = new Client("Test", utils.spawnClient("Test", "1", 4444));
			
			board.addClient(c);
            utils.sleep();
            
			assertEquals(board.getClientsTesting().get(0), c);
		}
		catch (IOException e1) {}
	}
	
	@Test
	public void addClonedClientTest() {
		try {
			TestUtils utils = new TestUtils();
			utils.startServer(1234);
			Whiteboard board = new Whiteboard();
			Client c1 = new Client("Joe", utils.spawnClient("Joe", "1", 1234));
			Client c2 = new Client("Joe", utils.spawnClient("Joe", "1", 1234));
			
			board.addClient(c1);
            utils.sleep();
			board.addClient(c2);
            utils.sleep();
            
			assertEquals(board.getClientsTesting().get(0), c1);
			assertEquals(board.getClientsTesting().get(1), c2);
		} catch (IOException e) {}
	}
	
	@Test
	public void removeClientBasicTest() {
		try {
		    System.out.println("------------------------------------");
			TestUtils utils = new TestUtils();
			utils.startServer(3000);
			Whiteboard board = new Whiteboard();
			Client c1 = new Client("Mike", utils.spawnClient("Mike", "1", 3000));
			Client c2 = new Client("Austin", utils.spawnClient("Austin", "1", 3000));
			
			board.addClient(c1);
            utils.sleep();
			board.addClient(c2);
            utils.sleep();
			board.removeClient(c1);
			utils.sleep();
			
			assertEquals(board.getClientsTesting().size(), 1);
			assertEquals(board.getClientsTesting().get(0), c2);
			System.out.println("------------------------------------");
		} catch (IOException e) {}
	}
	
	@Test
	public void removeNonexistentClientTest() {
		try {
			TestUtils utils = new TestUtils();
			utils.startServer(3333);
			Whiteboard board = new Whiteboard();
			Client c1 = new Client("Johannes", utils.spawnClient("Johannes", "1", 3333));
			Client c2 = new Client("Joe", utils.spawnClient("Joe", "1", 3333));
			
			board.addClient(c1);
            utils.sleep();
			board.removeClient(c2);
            utils.sleep();
            
			assertEquals(board.getClientsTesting().size(), 1);
			assertEquals(board.getClientsTesting().get(0), c1);
			
		} catch (IOException e) {}
	}
	
   @Test
    public void successfullySendsMessageToOneClient() {
        try {
            TestUtils utils = new TestUtils();
            utils.startServer(4445);
            Whiteboard board = new Whiteboard();
            Client c1 = new Client("Johannes", utils.spawnClient("Johanens", "1", 4445));
            
            board.addClient(c1);
            utils.sleep();
            board.addLine(sampleLineMessage);
            utils.sleep();   
            
            String expectedMessage = "[users Johannes, "+sampleLineMessage+"]";
            
            assertEquals(board.getClientsTesting().size(), utils.clientReceivedMessages.size());
            assertEquals(expectedMessage, utils.clientReceivedMessages.get(0).toString());
            
        } catch (IOException e) {}
    }
	    
   @Test
   public void successfullySendsMessageToManyClients() {
       try {
           TestUtils utils = new TestUtils();
           utils.startServer(4446);
           Whiteboard board = new Whiteboard();
           
           Client c1 = new Client("Johannes", utils.spawnClient("Johanens", "1", 4446));
           Client c2 = new Client("Joe", utils.spawnClient("Joe", "1", 4446));
           Client c3 = new Client("Austin", utils.spawnClient("Austin", "1", 4446));
           Client c4 = new Client("Mike", utils.spawnClient("Mike", "1", 4446));
           
           board.addClient(c1);
           utils.sleep();
           board.addClient(c2);
           utils.sleep();
           board.addClient(c3);
           utils.sleep();
           board.addClient(c4);
           utils.sleep();
           board.addLine(sampleLineMessage);
           utils.sleep();            
           
           assertEquals(board.getClientsTesting().size(), utils.clientReceivedMessages.size());
           
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
