package clientServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class ServerPool implements Runnable {

    private static final int DEFAULT_MAX_THREADS = 2;
    private static final int DEFAULT_SERVER_PORT = 9000;

    private int serverPort;
    protected ServerSocket serverSocket = null;
    protected Thread runningThread = null;
    protected ExecutorService threadPool;
    private Semaphore sem_threadPoolLimit;
    private boolean running;

    public ServerPool(int port){
        this(port, DEFAULT_MAX_THREADS);
    }

    public ServerPool(int port, int maxNumberOfThreads) {
        this.serverPort = port;
        sem_threadPoolLimit = new Semaphore(maxNumberOfThreads, true);
        threadPool = Executors.newCachedThreadPool();
//        threadPool = Executors.newFixedThreadPool(maxNumberOfThreads);
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
            Socket clientSocket;
            try {
                clientSocket = this.serverSocket.accept();

                sem_threadPoolLimit.acquire();
                this.threadPool.execute(new ServerWorker(clientSocket, serverNumber++, sem_threadPoolLimit));
            } catch (IOException e) {
                System.out.println("Error: could not accept client socket");
                stop();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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