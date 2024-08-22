import java.io.*;
import java.net.*;

public class Client1 {
    private Socket socket;
    private BufferedReader br;
    private PrintWriter out;
    private volatile boolean exitRequested = false; // Shared flag for exit

    public Client1() {
        try {
            System.out.println("Sending request to server....");
            socket = new Socket("127.0.0.1", 7777);
            System.out.println("Connection established.");

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            startReading();
            startWriting();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startReading() {
        Runnable r1 = () -> {
            System.out.println("Reader started....");

            try {
                while (!exitRequested) { // Check the exit flag
                    String msg = br.readLine();
                    if (msg == null || msg.equals("exit")) {
                        System.out.println("Server terminated the chat");
                        exitRequested = true; // Set the exit flag
                        socket.close();
                        break;
                    }
                    System.out.println("Server: " + msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        new Thread(r1).start();
    }

    public void startWriting() {
        Runnable r2 = () -> {
            System.out.println("Writer started....");

            try {
                while (!exitRequested) { // Check the exit flag
                    BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
                    String content = br1.readLine();

                    out.println(content);
                    out.flush();

                    if (content.equals("exit")) {
                        System.out.println("Exiting chat");
                        exitRequested = true; // Set the exit flag
                        socket.close();
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        new Thread(r2).start();
    }

    public static void main(String[] args) {
        System.out.println("Client");
        new Client();
    }
}
