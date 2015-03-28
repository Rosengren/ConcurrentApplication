package clientServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerPool implements Runnable {

    private static final int DEFAULT_MAX_THREADS = 2;
    private static final int DEFAULT_SERVER_PORT = 9000;

    private int serverPort;
    protected ServerSocket serverSocket = null;
    protected Thread runningThread = null;
//    protected ExecutorService threadPool = Executors.newCachedThreadPool();
    protected ExecutorService threadPool; //= Executors.newFixedThreadPool(DEFAULT_MAX_THREADS);
//    private Semaphore sem_threadPoolLimit;
    private boolean running;

    public ServerPool(int port){
        this(port, DEFAULT_MAX_THREADS);
    }

    public ServerPool(int port, int maxNumberOfThreads) {
        this.serverPort = port;
//        sem_threadPoolLimit = new Semaphore(DEFAULT_MAX_THREADS, true);
        threadPool = Executors.newFixedThreadPool(DEFAULT_MAX_THREADS);
        running = true;
    }

    public void setMaxThreads(int maxThreads) {
        threadPool = Executors.newFixedThreadPool(maxThreads);
    }


    @Override
    public void run() {

        int serverNumber = 1;

        synchronized(this) {
            this.runningThread = Thread.currentThread();
        }

        openSocket();
        while (isRunning()) { // TODO: change this to be able to close the socket
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
            } catch (IOException e) {
//                e.printStackTrace();
//                System.out.println("Error: could not accept client socket");
                stop();
            }

            this.threadPool.execute(new ServerWorker(clientSocket, serverNumber++));
        }

        this.threadPool.shutdown(); // Stopped Server
        closeSocket();
    }


    private void openSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error: could not open port " + this.serverPort);
        }
    }

    public synchronized void stop() {
        running = false;
        closeSocket();
    }

    public synchronized boolean isRunning() {
        return running;
    }

    public synchronized void closeSocket() {
        try {
            if (serverSocket != null)
                serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
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