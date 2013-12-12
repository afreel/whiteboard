package server;

import static org.junit.Assert.*;

import java.io.IOException;

import testing.TestUtils;

import org.junit.Test;

/*
 * TESTING STATEGY:
 * 
 * getUsername():
 * > Test that it will return the username of the client
 * 
 * sendMessage(String message):
 * > Test that we can send line messages to the client, and the client will receive it.
 * > Test that we can send new user messages to the client, and the client will receive it.
 * > Test that we can send user messages to the client, and that the client will successfully receive it.
 */
@nodidit
public class ClientTest {
	
	@Test
    public void clientGetUsernameTest() {
        try {
            TestUtils utils = new TestUtils();
            utils.startServer(4000);
            Client c = new Client("someNotSoLongUsername", utils.spawnClient(
                    "someNotSoLongUsername", "1", 4000));

            assertEquals(c.getUsername(), "someNotSoLongUsername");
        } catch (IOException e1) {
        }
    }

    @Test
    public void clientCanSendLineMessageTest() {
        String sampleLineMessage = "line 100 100 200 200 5 0 0 0";
        try {
            TestUtils utils = new TestUtils();
            utils.startServer(4001);
            Client c = new Client("Tester", utils.spawnClient("Tester", "1",
                    4001));

            c.sendMessage(sampleLineMessage);
            utils.sleep();

            String expected = "[" + sampleLineMessage + "]";
            assertEquals(expected, utils.clientReceivedMessages.get(0)
                    .toString());
        } catch (IOException e1) {
        }
    }

    @Test
    public void clientCanSendNewUserTest() {
        String sampleLineMessage = "newUser randomusername";
        try {
            TestUtils utils = new TestUtils();
            utils.startServer(4002);
            Client c = new Client("Tester", utils.spawnClient("Tester", "1",
                    4002));

            c.sendMessage(sampleLineMessage);
            utils.sleep();

            String expected = "[" + sampleLineMessage + "]";
            assertEquals(expected, utils.clientReceivedMessages.get(0)
                    .toString());
        } catch (IOException e1) {
        }
    }

    @Test
    public void clientCanSendUsersTest() {
        String sampleLineMessage = "users randomusername1 randomusername2";
        try {
            TestUtils utils = new TestUtils();
            utils.startServer(4003);
            Client c = new Client("Tester", utils.spawnClient("Tester", "1",
                    4003));

            c.sendMessage(sampleLineMessage);
            utils.sleep();

            String expected = "[" + sampleLineMessage + "]";
            assertEquals(expected, utils.clientReceivedMessages.get(0)
                    .toString());
        } catch (IOException e1) {
        }
    }
}
