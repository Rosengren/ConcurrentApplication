package clientServer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class Client implements Runnable {

    private static final int DEFAULT_SERVER_PORT = 9000;

    private int serverPort;
    private boolean running;

    private DataOutputStream serverOutputStream;
    private BufferedReader serverInputStream;

    public Client(int port) {
        serverPort = port;
        running = true;
    }

    @Override
    public void run() {

        String sentence;
        String modifiedSentence;
        Socket clientSocket = null;

        try {

            BufferedReader fromUser = new BufferedReader(new InputStreamReader(System.in));
            clientSocket = new Socket("localhost", serverPort);
            clientSocket.setSoTimeout(2000);
            serverOutputStream = new DataOutputStream(clientSocket.getOutputStream());
            serverInputStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            while (running) {
                sentence = fromUser.readLine();
                serverOutputStream.writeBytes(sentence + "\n");
                modifiedSentence = serverInputStream.readLine();
                System.out.println("FROM SERVER: " + modifiedSentence);
            }
        } catch (SocketTimeoutException e) {
            System.out.println("Connection Timed out. Server Pool must be full");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (clientSocket != null) {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean hasActiveConnection() {
        try {
            if (serverOutputStream == null || serverInputStream == null)
                return false;

            serverOutputStream.writeBytes("Test Active Connection\n");
            serverInputStream.readLine();
        } catch (SocketTimeoutException e){
            return false;
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    public static void main(String[] argv) {

        Client client;
        if (argv.length == 1)
            client = new Client(Integer.parseInt(argv[0]));
        else
            client = new Client(DEFAULT_SERVER_PORT);

        client.run();
    }
}