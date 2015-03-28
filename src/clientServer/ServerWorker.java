package clientServer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.Semaphore;

public class ServerWorker implements Runnable {

    private Socket clientSocket;
    private int serverID;
    private Semaphore semaphore;

    public ServerWorker(Socket clientSocket, int serverID, Semaphore semaphore) {
        this.clientSocket = clientSocket;
        this.serverID = serverID;
        this.semaphore = semaphore;
    }

    @Override
    public void run() {

        String clientInput;
        BufferedReader fromClient = null;
        DataOutputStream toClient = null;

        try {
            fromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            toClient = new DataOutputStream(clientSocket.getOutputStream());

            while(true) { // TODO: Change this so that thread
                clientInput = fromClient.readLine();
                toClient.writeBytes("Response from server thread: " + serverID + "\n");
                toClient.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fromClient != null) fromClient.close();
                if (toClient != null) toClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            semaphore.release();
        }
    }
}