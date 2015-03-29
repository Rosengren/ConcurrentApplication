import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class Client implements Runnable {

    private int serverPort;
    private boolean running;

    public Client(int port) {
        serverPort = port;
        running = true;
        printMsg("Client started...");
    }

    @Override
    public void run() {

        String sentence;
        String modifiedSentence;
        Socket clientSocket = null;

        printMsg("Ready to send to port " + serverPort + "...");
        try {
            BufferedReader fromUser = new BufferedReader(new InputStreamReader(System.in));
            printMsg("Input Message:");
            clientSocket = new Socket("localhost", serverPort);
            clientSocket.setSoTimeout(2000);
            DataOutputStream serverOutputStream = new DataOutputStream(clientSocket.getOutputStream());
            BufferedReader serverInputStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            while (running) {

                sentence = fromUser.readLine();
                serverOutputStream.writeBytes(sentence + "\n");

                try {
                    modifiedSentence = serverInputStream.readLine();
                    printMsg("Received Message from Server: " + modifiedSentence);
                } catch (SocketTimeoutException e) {
                    printMsg("Connection Timed out. Server Pool must be full");
                }
            }
        } catch (SocketException e) {
          System.out.println("Error: Socket Exception");
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

    // Can be used to save messages in log files
    // instead of outputting to the terminal
    private void printMsg(String msg) {
        System.out.println(msg);
    }

    public static void main(String[] argv) {

        Client client;
        if (argv.length == 1)
            client = new Client(Integer.parseInt(argv[0]));
        else
            client = new Client(Preferences.DEFAULT_SERVER_PORT);

        client.run();
    }
}