import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client {

    public static void main(String[] argv) throws Exception {
        System.out.println("Client Started...");
        int port = 6789;
        String sentence;
        String modifiedSentence;
        BufferedReader fromUser = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Input Message:");
        Socket clientSocket = new Socket("localhost", port);
        DataOutputStream toServer = new DataOutputStream(clientSocket.getOutputStream());
        BufferedReader fromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        sentence = fromUser.readLine();
        toServer.writeBytes(sentence + "\n");
        modifiedSentence = fromServer.readLine();
        System.out.println("FROM SERVER: " + modifiedSentence);
        clientSocket.close();
    }
}