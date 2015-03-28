package tests;

import clientServer.Client;
import clientServer.ServerPool;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ClientServerConnectionPoolTest {

    private static final int PORT = 9000;
    private static final int MAXIMUM_NUMBER_OF_THREADS = 2;

//    private Client client;
    private ServerPool serverPool;
    private Thread serverPoolThread;

    @Before
    public void setUp() {
//        client = new Client(PORT);
        System.out.println("init serverPool");
        serverPool = new ServerPool(PORT, MAXIMUM_NUMBER_OF_THREADS);
        (new Thread(serverPool)).start();
//        serverPoolThread.start();
        Client c = new Client(PORT);
        (new Thread(c)).start();

    }

    @Test
    public void testMaxiumThreads() {
//
//
//        // Create client threads
//        // THREAD POOL
        System.out.println("Creating Threads");
////        Thread serverPool = new Thread(new Client(PORT));
////        serverPool.start();
////        Thread[] clientThreads = new Thread[MAXIMUM_NUMBER_OF_THREADS];
//
//        System.out.println("Created all Threads");
//        for (int i = 0; i < MAXIMUM_NUMBER_OF_THREADS; i++) {
//            Client c = new Client(PORT);
//            new Thread(c).start();
//
////            clientThreads[i] = new Thread(c);
////            clientThreads[i].start();
//            System.out.println("Asserting active connection");
//            System.out.println(c.hasActiveConnection());
//            assertTrue(c.hasActiveConnection());
//            System.out.println("PASSED THE ASSERTION!");
//        }
//
//        System.out.println("Testing extra client");
//        Client extraClient = new Client(PORT);
//        Thread extraClientThread = new Thread(extraClient);
//        extraClientThread.start();
//        assertFalse(extraClient.hasActiveConnection());
    }

    @After
    public void tearDown() {
//        serverPool.stop();
    }
}
