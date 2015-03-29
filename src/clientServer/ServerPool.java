package clientServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ServerPool implements Runnable {

    private int serverPort;
    protected ServerSocket serverSocket = null;
    protected Thread runningThread = null;
    protected ExecutorService threadConnectionPool;
    private Semaphore sem_threadPoolLimit;
    private boolean running;

    // Callable and Future
    List<Future<String>> futureList;

    public ServerPool(int port){
        this(port, Preferences.DEFAULT_MAX_THREADS);
    }

    public ServerPool(int port, int maxNumberOfThreads) {
        this.serverPort = port;
        sem_threadPoolLimit = new Semaphore(maxNumberOfThreads, true);
        threadConnectionPool = Executors.newCachedThreadPool();
        running = true;

        futureList = new ArrayList<Future<String>>();

        setShutdownBehaviour();
        printMsg("Server pool started...");
        printMsg("Listening on Port " + port);
    }

    @Override
    public void run() {

        int threadNumber = 1;

        synchronized(this) {
            this.runningThread = Thread.currentThread();
        }

        openSocket();
        Socket clientSocket;
        while (isRunning()) {
            try {
                clientSocket = this.serverSocket.accept();

                sem_threadPoolLimit.acquire();

                // use thread from connection pool
                Callable<String> serverWorker = new ServerWorker(clientSocket, threadNumber++, sem_threadPoolLimit);
                futureList.add(threadConnectionPool.submit(serverWorker));

            } catch (RuntimeException e) {
                printMsg("Error: runtime Exception");
                stop();
            } catch (IOException e) {
                printMsg("Error: could not accept client socket");
                stop();
            } catch (InterruptedException e) {
                printMsg("Error: worker thread interrupted");
                e.printStackTrace();
            }
        }

    }


    private void openSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            e.printStackTrace();
            printMsg("Error: could not open port " + this.serverPort);
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

    public void setShutdownBehaviour() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {

                try {
                    threadConnectionPool.shutdown(); // Stopped Server
                    threadConnectionPool.awaitTermination(20, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                printMsg("Results:");
                for(Future<String> future : futureList){
                    try {
                        printMsg(future.get());
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                closeSocket();
            }
        });
    }


    // Can be used to save messages in log files
    // instead of outputting to the terminal
    private void printMsg(String msg) {
        System.out.println(msg);
    }


    public static void main(java.lang.String argv[]) {

        ServerPool server;
        if (argv.length == 1)
            server = new ServerPool(Integer.parseInt(argv[0]));
        else
            server = new ServerPool(Preferences.DEFAULT_SERVER_PORT);

        new Thread(server).start();
    }
}