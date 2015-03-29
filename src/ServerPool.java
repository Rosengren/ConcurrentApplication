import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ServerPool implements Runnable {

    private int serverPort;
    private ServerSocket serverSocket = null;
    private ExecutorService threadConnectionPool;
    private Semaphore sem_threadPoolLimit;
    private boolean running;

    private Socket clientSocket;

    private ArrayList<Socket> openSockets;

    // Callable and Future
    private List<Future<String>> futureList;

    public ServerPool(int port){
        this(port, Preferences.DEFAULT_MAX_THREADS);
    }

    public ServerPool(int port, int maxNumberOfThreads) {
        this.serverPort = port;
        sem_threadPoolLimit = new Semaphore(maxNumberOfThreads, true);
        threadConnectionPool = Executors.newCachedThreadPool();
        running = true;

        openSockets= new ArrayList<Socket>();
        futureList = new ArrayList<Future<String>>();

        setShutdownBehaviour();
        printMsg("Server pool started...");
        printMsg("Listening on Port " + port);
    }

    @Override
    public void run() {

        synchronized(this) {
            Thread runningThread = Thread.currentThread();
        }

        openSocket();
        while (isRunning()) {
            try {
                clientSocket = this.serverSocket.accept();
                openSockets.add(clientSocket);

                sem_threadPoolLimit.acquire();

                // use thread from connection pool
                Callable<String> serverWorker = new WorkerThread(clientSocket, sem_threadPoolLimit);

                futureList.add(threadConnectionPool.submit(serverWorker));

            } catch (RuntimeException e) {
                printMsg("Error: runtime Exception");
                stop();
            } catch (IOException e) {
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
                    for (Socket socket : openSockets)
                        socket.close();

                    printMsg("Stopping Server...");
                    threadConnectionPool.shutdown();
                    printMsg("Waiting " + Preferences.TERMINATION_WAIT_TIME + " seconds");
                    threadConnectionPool.awaitTermination(Preferences.TERMINATION_WAIT_TIME,
                            TimeUnit.SECONDS);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                printMsg("Results:");
                for(Future<String> future : futureList){
                    try {
                        if (future.isDone() || future.isCancelled())
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
        else if (argv.length == 2)
            server = new ServerPool(Integer.parseInt(argv[0]), Integer.parseInt(argv[1]));
        else
            server = new ServerPool(Preferences.DEFAULT_SERVER_PORT);

        new Thread(server).start();
    }
}