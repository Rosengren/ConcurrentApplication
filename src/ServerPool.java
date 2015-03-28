import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class ServerPool implements Runnable {

    private static final int MAX_THREADS = 2;
    private static final int DEFAULT_SERVER_PORT = 9000;

    private int serverPort;
    protected ServerSocket serverSocket = null;
    protected Thread runningThread = null;
//    protected ExecutorService threadPool = Executors.newCachedThreadPool();
    protected ExecutorService threadPool = Executors.newFixedThreadPool(MAX_THREADS);
    private Semaphore sem_threadPoolLimit;
    private boolean running;

    public ServerPool(int port){
        this.serverPort = port;
        sem_threadPoolLimit = new Semaphore(MAX_THREADS, true);
        running = true;
    }


    @Override
    public void run() {

        int serverNumber = 1;

        synchronized(this) {
            this.runningThread = Thread.currentThread();
        }

        openSocket();
        while (running) { // TODO: change this to be able to close the socket
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error: could not accept client socket");
            }

            this.threadPool.execute(new ServerWorker(clientSocket, serverNumber++));
        }

        this.threadPool.shutdown(); // Stopped Server
    }


    private void openSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error: could not open port " + this.serverPort);
        }
    }


    public static void main(String argv[]) {

        ServerPool server;
        if (argv.length == 1)
            server = new ServerPool(Integer.parseInt(argv[0]));
        else
            server = new ServerPool(DEFAULT_SERVER_PORT);

        new Thread(server).start();
    }
}