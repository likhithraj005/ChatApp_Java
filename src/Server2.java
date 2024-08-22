import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.text.*;
import java.awt.Color;

public class Server2 extends JFrame {
    private ServerSocket server;
    private Socket socket;
    private BufferedReader br;
    private PrintWriter out;
    private JTextPane messageArea;
    private JTextField messageInput;

    private StyledDocument doc;
    private SimpleAttributeSet clientStyle;
    private SimpleAttributeSet serverStyle;
    private int messagePosition = 0; // Track the message position

    public Server2() {
        try {
            server = new ServerSocket(7777);
            System.out.println("Server is ready to accept connections");
            System.out.println("Waiting for a client...");

            socket = server.accept();
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            createGUI();
            handleEvents();

            startReading();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createGUI() {
        setTitle("Server");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        messageArea = new JTextPane();
        doc = messageArea.getStyledDocument();
        messageInput = new JTextField();

        JScrollPane scrollPane = new JScrollPane(messageArea);
        messageArea.setEditable(false);

        add(scrollPane, BorderLayout.CENTER);
        add(messageInput, BorderLayout.SOUTH);

        // Initialize text attributes for styling
        clientStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(clientStyle, Color.BLUE);

        serverStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(serverStyle, Color.RED);

        setVisible(true);
    }

    private void handleEvents() {
        messageInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String contentToSend = messageInput.getText();
                appendMessage("Server: " + contentToSend + "\n", serverStyle);
                out.println(contentToSend);
                out.flush();
                messageInput.setText("");
            }
        });
    }

    private void appendMessage(String message, AttributeSet style) {
        try {
            doc.insertString(messagePosition, message, style);
            messagePosition += message.length();
            // Scroll to the end of the document to show the latest messages
            messageArea.setCaretPosition(doc.getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void startReading() {
        Runnable r1 = () -> {
            try {
                while (true) {
                    String msg = br.readLine();
                    if (msg == null || msg.equals("exit")) {
                        System.out.println("Client terminated the chat");
                        socket.close();
                        break;
                    }
                    appendMessage("Client: " + msg + "\n", clientStyle);
                }
            } catch (IOException e) {
                System.out.println("Connection is closed!");
            }
        };
        new Thread(r1).start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Server2 server2 = new Server2();
        });
    }
}
