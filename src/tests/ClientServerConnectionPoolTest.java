package tests;

import clientServer.*;


public class ClientServerConnectionPoolTest {

    private static final int PORT = 9000;

    private Client client;
    private ServerPool serverPool;

    public void setUp() {
        client = new Client(PORT);
        serverPool = new ServerPool(PORT);
    }

    public void testClientConnection() {

    }
}
