import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String argv[]) throws Exception {
        System.out.println("Starting...");
        int port = 6789;
        String clientSentence;
        String capitalizedSentence;
        ServerSocket socket = new ServerSocket(port);

        while (true) {
            Socket connectionSocket = socket.accept();
            BufferedReader fromClient =
                    new BufferedReader((new InputStreamReader(connectionSocket.getInputStream())));
            DataOutputStream toClient = new DataOutputStream(connectionSocket.getOutputStream());
            clientSentence = fromClient.readLine();
            System.out.println("Received: " + clientSentence);
            capitalizedSentence = clientSentence.toUpperCase() +"\n";
            toClient.writeBytes(capitalizedSentence);

        }
    }
}