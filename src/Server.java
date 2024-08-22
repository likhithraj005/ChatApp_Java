import java.io.*;
import java.net.*;
class Server
{
    ServerSocket server;
    Socket socket;
    BufferedReader br;
    PrintWriter out;

        //constructor
        public Server() {
            try {
                server = new ServerSocket(7777);
                System.out.println("Server is ready to accept connections");
                System.out.println("Waiting for a client...");

                // Accept a client connection and assign it to the 'socket' field
                socket = server.accept();

                // Initialize input and output streams
                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                startReading();
                startWriting();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    public void startReading() {
            // thread-read karke deta rahega
            Runnable r1 = () -> {
                System.out.println("Reader started....");

                try{

                while (true) {


                        //accepting the sender
                        String msg = br.readLine();
                        if (msg.equals("exit")) {
                            System.out.println("Client terminated the chat");

                            socket.close();

                            break;
                        }

                        System.out.println("Client : " + msg);
                    }


                }catch (Exception e){
//                    e.printStackTrace();
                    System.out.println("Connection is closed!");
                }

            };
            new Thread(r1).start();
        }

        public void startWriting(){
            // thread - data user lega and the send karega client tak
            Runnable r2=()-> {
                System.out.println("Writer started....");

                try{
                    //while (true && !socket.isClosed())
                while (!socket.isClosed()) {

                        //sender
                        BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
                        String content = br1.readLine();


                        out.println(content);
                        out.flush();

                        if(content.equals("exit")){
                            socket.close();
                            break;
                        }

                    }
                }catch (Exception e){
//                    e.printStackTrace();
                    System.out.println("Connection is closed!");
                }

            };
            new Thread(r2).start();
        }


        public static void main(String[] args) {
            System.out.println("Server");
            new Server();
        }
}

