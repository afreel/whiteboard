package server;

import static org.junit.Assert.*;

import java.io.IOException;
import testing.TestUtils;
import org.junit.Test;

/**
 * Tests our Whiteboard class
 *  @category no_didit
 */

public class WhiteboardTest {
   
	/*TESTING STRATEGY:
	 * 
	 * addClient(Client c): 
	 * > ensure client is added to the clients list
	 * > test case when clone of Client c already exists in clients list
	 * > ensure client gets back a user message containing its own username 
	 *   when it connects to a board
	 * > ensure other clients get a new user message when a new client connects
	 * 
	 * removeClient(Client c):
	 * > ensure client is removed from the clients list
	 * > test case when Client c is not in the clients list
	 * > ensure other clients get a remove user message to know that they
	 *   shouldn't display this users name any longer
	 *   
	 * addLine(String message):
	 * > check that line is added to history list
	 * > test case when message is empty
	 * > test case when message is abstract format
	 * > check that line message gets echoed back to the client that sent it
	 * > check that all other clients on the whiteboard also get the line message.
	 * 
	 */
	
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
        } catch (IOException e1) {
        }
    }

    @Test
    public void whiteboardSendsUserMessageWhenClientConnectsTest() {
        try {
            TestUtils utils = new TestUtils();
            utils.startServer(4443);
            Whiteboard board = new Whiteboard();
            Client c = new Client("Tester", utils.spawnClient("Tester", "1",
                    4443));

            board.addClient(c);
            utils.sleep();

            String expectedMessage = "[users Tester]";
            assertEquals(expectedMessage, utils.clientReceivedMessages.get(0)
                    .toString());
        } catch (IOException e1) {
        }
    }

    @Test
    public void whiteboardUpdatesAllOtherClientsWhenNewClientConnectsTest() {
        try {
            TestUtils utils = new TestUtils();
            utils.startServer(4442);
            Whiteboard board = new Whiteboard();
            Client c1 = new Client("Tester", utils.spawnClient("Tester", "1",
                    4442));
            Client c2 = new Client("SecondTester", utils.spawnClient(
                    "SecondTester", "1", 4442));
            Client c3 = new Client("ThirdTester", utils.spawnClient(
                    "ThirdTester", "1", 4442));

            board.addClient(c1);
            utils.sleep();
            board.addClient(c2);
            utils.sleep();
            board.addClient(c3);
            utils.sleep();
            
            String expectedMessages1 = "[users Tester, newUser SecondTester, newUser ThirdTester]";
            String expectedMessages2 = "[users Tester SecondTester, newUser ThirdTester]";
            String expectedMessages3 = "[users Tester SecondTester ThirdTester]";
            
            assertEquals(expectedMessages1, utils.clientReceivedMessages.get(0)
                    .toString());
            assertEquals(expectedMessages2, utils.clientReceivedMessages.get(1)
                    .toString());
            assertEquals(expectedMessages3, utils.clientReceivedMessages.get(2)
                    .toString());
        } catch (IOException e1) {
        }
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
        } catch (IOException e) {
        }
    }

    @Test
    public void removeClientBasicTest() {
        try {
            TestUtils utils = new TestUtils();
            utils.startServer(3000);
            Whiteboard board = new Whiteboard();
            Client c1 = new Client("Mike", utils.spawnClient("Mike", "1", 3000));
            Client c2 = new Client("Austin", utils.spawnClient("Austin", "1",
                    3000));

            board.addClient(c1);
            utils.sleep();
            board.addClient(c2);
            utils.sleep();
            board.removeClient(c1);
            utils.sleep();

            assertEquals(board.getClientsTesting().size(), 1);
            assertEquals(board.getClientsTesting().get(0), c2);
        } catch (IOException e) {
        }
    }

    @Test
    public void removeNonexistentClientTest() {
        try {
            TestUtils utils = new TestUtils();
            utils.startServer(3333);
            Whiteboard board = new Whiteboard();
            Client c1 = new Client("Johannes", utils.spawnClient("Johannes",
                    "1", 3333));
            Client c2 = new Client("Joe", utils.spawnClient("Joe", "1", 3333));

            board.addClient(c1);
            utils.sleep();
            board.removeClient(c2);
            utils.sleep();

            assertEquals(board.getClientsTesting().size(), 1);
            assertEquals(board.getClientsTesting().get(0), c1);

        } catch (IOException e) {
        }
    }

    @Test
    public void whiteboardUpdatesAllOtherClientsUponRemoveClientTest() {
        try {
            TestUtils utils = new TestUtils();
            utils.startServer(4441);
            Whiteboard board = new Whiteboard();
            Client c1 = new Client("Tester", utils.spawnClient("Tester", "1",
                    4441));
            Client c2 = new Client("SecondTester", utils.spawnClient(
                    "SecondTester", "1", 4441));
            Client c3 = new Client("ThirdTester", utils.spawnClient(
                    "ThirdTester", "1", 4441));

            board.addClient(c1);
            utils.sleep();
            board.addClient(c2);
            utils.sleep();
            board.addClient(c3);
            utils.sleep();
            board.removeClient(c3);
            utils.sleep();
            board.addLine(sampleLineMessage);
            utils.sleep();
            
            String expectedMessages1 = "[users Tester, newUser SecondTester, newUser ThirdTester"
                    + ", removeUser ThirdTester, "+sampleLineMessage+"]";
            String expectedMessages2 = "[users Tester SecondTester, newUser ThirdTester"
                    + ", removeUser ThirdTester, "+sampleLineMessage+"]";
            String expectedMessages3 = "[users Tester SecondTester ThirdTester]";

            assertEquals(expectedMessages1, utils.clientReceivedMessages.get(0)
                    .toString());
            assertEquals(expectedMessages2, utils.clientReceivedMessages.get(1)
                    .toString());
            assertEquals(expectedMessages3, utils.clientReceivedMessages.get(2)
                    .toString());
        } catch (IOException e1) {
        }
    }

    @Test
    public void removedClientDoesntGetDrawMessagesAfterRemoval() {
        try {
            TestUtils utils = new TestUtils();
            utils.startServer(4440);
            Whiteboard board = new Whiteboard();
            Client c1 = new Client("Tester", utils.spawnClient("Tester", "1",
                    4440));
            Client c2 = new Client("SecondTester", utils.spawnClient(
                    "SecondTester", "1", 4440));
            Client c3 = new Client("ThirdTester", utils.spawnClient(
                    "ThirdTester", "1", 4440));

            board.addClient(c1);
            utils.sleep();
            board.addClient(c2);
            utils.sleep();
            board.addClient(c3);
            utils.sleep();
            board.removeClient(c3);
            utils.sleep();
            board.addLine(sampleLineMessage);
            utils.sleep();

            String expectedMessages1 = "[users Tester, newUser SecondTester, newUser ThirdTester"
                    + ", removeUser ThirdTester, " + sampleLineMessage + "]";
            String expectedMessages2 = "[users Tester SecondTester ThirdTester]";

            assertEquals(expectedMessages1, utils.clientReceivedMessages.get(0)
                    .toString());
            assertEquals(expectedMessages2, utils.clientReceivedMessages.get(2)
                    .toString());

        } catch (IOException e1) {
        }
    }

    @Test
    public void successfullyEchoesMessageToClient() {
        try {
            TestUtils utils = new TestUtils();
            utils.startServer(4445);
            Whiteboard board = new Whiteboard();
            Client c1 = new Client("Johannes", utils.spawnClient("Johanens",
                    "1", 4445));

            board.addClient(c1);
            utils.sleep();
            board.addLine(sampleLineMessage);
            utils.sleep();

            String expectedMessage = "[users Johannes, " + sampleLineMessage
                    + "]";

            assertEquals(board.getClientsTesting().size(),
                    utils.clientReceivedMessages.size());
            assertEquals(expectedMessage, utils.clientReceivedMessages.get(0)
                    .toString());

        } catch (IOException e) {
        }
    }

    @Test
    public void successfullyDistributesMessageToManyClients() {
        try {
            TestUtils utils = new TestUtils();
            utils.startServer(4446);
            Whiteboard board = new Whiteboard();

            Client c1 = new Client("Johannes", utils.spawnClient("Johanens",
                    "1", 4446));
            Client c2 = new Client("Joe", utils.spawnClient("Joe", "1", 4446));
            Client c3 = new Client("Austin", utils.spawnClient("Austin", "1",
                    4446));
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

            assertEquals(board.getClientsTesting().size(),
                    utils.clientReceivedMessages.size());

        } catch (IOException e) {
        }
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
