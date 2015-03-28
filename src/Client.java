import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client implements Runnable {

    private static final int DEFAULT_SERVER_PORT = 9000;

    private int serverPort;
    private boolean running;

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

            DataOutputStream toServer = new DataOutputStream(clientSocket.getOutputStream());

            BufferedReader fromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            while (running) {
                sentence = fromUser.readLine();
                toServer.writeBytes(sentence + "\n");
                modifiedSentence = fromServer.readLine();
                System.out.println("FROM SERVER: " + modifiedSentence);
            }

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

    public static void main(String[] argv) {

        Client client;

        if (argv.length == 1) {
            client = new Client(Integer.parseInt(argv[0]));
        } else {
            client = new Client(DEFAULT_SERVER_PORT);
        }

        client.run();
    }
}