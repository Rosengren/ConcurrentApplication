package clientServer;

public class Main {

    public static void main(String argv[]) {
        ServerPool server = new ServerPool(9000);
        new Thread(server).start();
    }
}
