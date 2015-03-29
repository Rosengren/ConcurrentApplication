import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;

public class WorkerThread implements Callable<String> {

    private Socket clientSocket;
    private Semaphore semaphore;
    private boolean running;

    BufferedReader fromClient;
    DataOutputStream toClient;

    public WorkerThread(Socket clientSocket, Semaphore semaphore) {
        this.clientSocket = clientSocket;
        this.semaphore = semaphore;
        running = true;
    }

    @Override
    public String call() throws Exception {
        ArrayList<String> clientMessages = new ArrayList<String>();
        String clientInput;

        fromClient = null;
        toClient = null;

        long threadID = Thread.currentThread().getId();

        try {
            fromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            toClient = new DataOutputStream(clientSocket.getOutputStream());

            while (running) {
                clientInput = fromClient.readLine();
                if (clientInput != null) {// ignore closing connection message
                    clientMessages.add(clientInput);
                }

                printMsg("Received this message from client: '" + clientInput + "' on thread " + threadID + "\n" +
                        "Sending response to client...");

                toClient.writeBytes(clientInput + "!!\n");
                toClient.flush();

            }

        } catch (IOException e) {
            printMsg("Client disconnected. Releasing Thread " + threadID);
            stop();
        } catch (Exception e) {
            printMsg("SOMETHING HAPPENED");
        } finally {
            try {
                if (fromClient != null) fromClient.close();
                if (toClient != null) toClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            semaphore.release();
        }

        String result = "Received the following messages from clients on thread " + threadID + ": \n";
        for (String msg : clientMessages) {
            result += "\t" + msg + "\n";
        }

        return result;
    }

    private void printMsg(String msg) {
        System.out.println(msg);
    }

    public synchronized void stop() {
        running = false;
    }
}