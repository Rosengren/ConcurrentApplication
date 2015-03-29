package clientServer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;

public class ServerWorker implements Callable<String> {

    private int threadID;
    private Socket clientSocket;
    private Semaphore semaphore;
    private boolean running;

    public ServerWorker(Socket clientSocket, int serverID, Semaphore semaphore) {
        this.clientSocket = clientSocket;
        this.threadID = serverID;
        this.semaphore = semaphore;
        running = true;
    }

    @Override
    public String call() throws Exception {
        ArrayList<String> clientMessages = new ArrayList<String>();
        String clientInput;
        BufferedReader fromClient = null;
        DataOutputStream toClient = null;

        try {
            fromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            toClient = new DataOutputStream(clientSocket.getOutputStream());

            while (running) {
                clientInput = fromClient.readLine();
                if (clientInput != null) {// ignore closing connection message
                    clientMessages.add(clientInput);

                    printMsg("Received this message from client: '" + clientInput + "' on the " + threadID + "\n" +
                            "Sending response to client...");

                    toClient.writeBytes(clientInput + "!!\n");
                    toClient.flush();
                }
            }

        } catch (IOException e) {
            printMsg("Client disconnected from Thread " + threadID);
            stop();
        }
        finally {
            try {
                if (fromClient != null) fromClient.close();
                if (toClient != null) toClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            semaphore.release();
        }

        String result = "Received the following messages from clients: \n";
        for (String msg : clientMessages) {
            result += "\t" + msg + "\n";
        }

        return result;
    }

    // Can be used to save messages in log files
    // instead of outputting to the terminal
    private void printMsg(String msg) {
        System.out.println(msg);
    }

    private synchronized void stop() {
        running = false;
    }
}