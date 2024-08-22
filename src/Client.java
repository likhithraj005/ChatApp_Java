import java.io.*;
import java.net.*;

public class Client {
    private Socket socket;
    private BufferedReader br;
    private PrintWriter out;

    public Client() {
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
                while (true) {
                    String msg = br.readLine();
                    if (msg == null || msg.equals("exit")) {
                        System.out.println("Server terminated the chat");
                        socket.close();
                        break;
                    }
                    System.out.println("Server: " + msg);
                }
            } catch (IOException e) {
//                e.printStackTrace();
                System.out.println("Connection is closed!");
            }
        };
        new Thread(r1).start();
    }

    public void startWriting() {
        Runnable r2 = () -> {
            System.out.println("Writer started....");
            try {
                //while (true && !socket.isClosed())
                while (!socket.isClosed()) {
                    // Read user input from the console in a separate thread
                    BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
                    String content = br1.readLine();
                    out.println(content);
                    out.flush();

                    if(content.equals("exit")){
                        socket.close();
                        break;
                    }
                }

                //System.out.println("Connection is closed!");
            } catch (IOException e) {
                //e.printStackTrace();
            }
        };
        new Thread(r2).start();
    }

    public static void main(String[] args) {
        System.out.println("Client");
        new Client();
    }
}
