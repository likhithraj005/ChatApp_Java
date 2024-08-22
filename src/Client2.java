import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.text.*;
import java.awt.Color;

public class Client2 extends JFrame {
    private Socket socket;
    private BufferedReader br;
    private PrintWriter out;
    private JTextPane messageArea;
    private JTextField messageInput;

    private StyledDocument doc;
    private SimpleAttributeSet clientStyle;
    private SimpleAttributeSet serverStyle;

    public Client2() {
        try {
            System.out.println("Sending request to server....");
            socket = new Socket("127.0.0.1", 7777);
            System.out.println("Connection established.");

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
        setTitle("Client");
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
                appendMessage("Me: " + contentToSend + "\n", clientStyle);
                out.println(contentToSend);
                out.flush();
                messageInput.setText("");

                // Ensure that the caret is at the end (bottom-right)
                SwingUtilities.invokeLater(() -> {
                    messageInput.setCaretPosition(messageInput.getText().length());
                });
            }
        });
    }

    private void appendMessage(String message, AttributeSet style) {
        try {
            doc.insertString(doc.getLength(), message, style);
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
                        System.out.println("Server terminated the chat");
                        JOptionPane.showMessageDialog(this, "Server Terminated the chat");
                        messageInput.setEnabled(false);
                        socket.close();
                        break;
                    }
                    appendMessage(msg + "\n", serverStyle);
                }
            } catch (IOException e) {
                System.out.println("Connection is closed!");
            }
        };
        new Thread(r1).start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Client2());
    }
}
