package com.mycompany.poe;

// Import necessary libraries
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class POE {

    // GUI components
    private static JPanel chatPanel;
    private static JTextField userInput;

    // Date formatter
    private static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    // User details
    private static String currentUser;

    // File paths for storing data
    private static final String USERS_FILE = "users.txt";
    private static final String MESSAGES_FILE = "messages.txt";

    // AI Chatbot Responses
    private static final Map<String, String> aiResponses = new HashMap<>();

    public static void main(String[] args) {
        // Apply a modern look and feel for the GUI
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to apply Nimbus look and feel.");
        }

        // Initialize AI chatbot responses
        initializeAIResponses();

        // Launch the login screen
        SwingUtilities.invokeLater(POE::createLoginScreen);
    }

    // Initialize chatbot responses
    private static void initializeAIResponses() {
        aiResponses.put("hi", "Hello! How can I assist you today?");
        aiResponses.put("how are you", "I'm just a program, but I'm functioning as expected! How about you?");
        aiResponses.put("what is your name", "I am your friendly chatbot!");
        aiResponses.put("bye", "Goodbye! Have a great day!");
        aiResponses.put("help", "Sure! I can assist you with your queries. Just type your question.");
    }

    // Create the login screen
    private static void createLoginScreen() {
        JFrame loginFrame = new JFrame("Login");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(400, 300);
        loginFrame.setLayout(new BorderLayout());
        loginFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel userLabel = new JLabel("Username:");
        JTextField userText = new JTextField(15);
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passText = new JPasswordField(15);
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(userLabel, gbc);

        gbc.gridx = 1;
        panel.add(userText, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(passLabel, gbc);

        gbc.gridx = 1;
        panel.add(passText, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(loginButton, gbc);

        gbc.gridy = 3;
        panel.add(registerButton, gbc);

        loginFrame.add(panel, BorderLayout.CENTER);
        loginFrame.setVisible(true);

        // Login button action
        loginButton.addActionListener(e -> {
            String username = userText.getText();
            String password = new String(passText.getPassword());

            if (authenticateUser(username, password)) {
                currentUser = username;
                loginFrame.dispose();
                startChat(); // Launch ChatScreen upon successful login
            } else {
                JOptionPane.showMessageDialog(loginFrame, "Invalid username or password.", "Login Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Register button action
        registerButton.addActionListener(e -> {
            loginFrame.dispose();
            showRegistrationScreen();
        });
    }

    // Registration screen
    private static void showRegistrationScreen() {
        JFrame registerFrame = new JFrame("Register");
        registerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        registerFrame.setSize(400, 350);
        registerFrame.setLayout(new BorderLayout());
        registerFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel userLabel = new JLabel("Username:");
        JTextField userText = new JTextField(15);
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passText = new JPasswordField(15);
        JLabel phoneLabel = new JLabel("Phone:");
        JTextField phoneText = new JTextField(15);
        JButton registerButton = new JButton("Register");

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(userLabel, gbc);

        gbc.gridx = 1;
        panel.add(userText, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(passLabel, gbc);

        gbc.gridx = 1;
        panel.add(passText, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(phoneLabel, gbc);

        gbc.gridx = 1;
        panel.add(phoneText, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(registerButton, gbc);

        registerFrame.add(panel, BorderLayout.CENTER);
        registerFrame.setVisible(true);

        // Register button action
        registerButton.addActionListener(e -> {
            String username = userText.getText();
            String password = new String(passText.getPassword());
            String phone = phoneText.getText();

            if (registerUser(username, password, phone)) {
                JOptionPane.showMessageDialog(registerFrame, "Registration successful! You can now log in.", "Success", JOptionPane.INFORMATION_MESSAGE);
                registerFrame.dispose();
                createLoginScreen();
            } else {
                JOptionPane.showMessageDialog(registerFrame, "Registration failed. Username may already exist.", "Registration Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    // Register a new user
    private static boolean registerUser(String username, String password, String phone) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE, true))) {
            // Check if the username already exists
            if (isUsernameTaken(username)) {
                return false;
            }

            // Save the user to the file
            writer.write(username + "," + password + "," + phone);
            writer.newLine();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Authenticate a user
    private static boolean authenticateUser(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3 && parts[0].equals(username) && parts[1].equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Check if a username is already taken
    private static boolean isUsernameTaken(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 0 && parts[0].equals(username)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Start the chat screen
    private static void startChat() {
        JFrame frame = new JFrame("Chat - " + currentUser);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 800);
        frame.setLayout(new BorderLayout());

        // Chat Panel
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(chatPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        JPanel inputPanel = new JPanel(new BorderLayout());
        userInput = new JTextField();
        JButton sendButton = new JButton("Send");

        inputPanel.add(userInput, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(inputPanel, BorderLayout.SOUTH);

        frame.setVisible(true);

        // Send welcome message
        appendMessage("System", "Welcome, " + currentUser + "!");

        // Action listeners for sending messages
        sendButton.addActionListener((ActionEvent e) -> sendMessage());
        userInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }
        });
    }

    // Append a message to the chat panel
    private static void appendMessage(String sender, String message) {
        JLabel messageLabel = new JLabel(sender + ": " + message);
        chatPanel.add(messageLabel);
        chatPanel.revalidate();
        chatPanel.repaint();
    }

    // Handle sending messages
    private static void sendMessage() {
        String message = userInput.getText();
        if (!message.isEmpty()) {
            appendMessage(currentUser, message);
            saveMessage(currentUser, message);
            simulateBotResponse(message);
            userInput.setText("");
        }
    }

    // Simulate a chatbot response
    private static void simulateBotResponse(String userInput) {
        String lowerCaseInput = userInput.toLowerCase();
        String botMessage = aiResponses.getOrDefault(lowerCaseInput, "I'm not sure how to respond to that. Can you clarify?");
        appendMessage("Bot", botMessage);
        saveMessage("Bot", botMessage);
    }

    // Save messages to a text file
    private static void saveMessage(String sender, String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(MESSAGES_FILE, true))) {
            writer.write(sender + ": " + message + " (" + timeFormat.format(System.currentTimeMillis()) + ")");
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// References:
// 1. Oracle Corporation (2025) 'Java Swing Documentation'. Available at: https://docs.oracle.com/javase/8/docs/technotes/guides/swing/index.html (Accessed: 14 April 2025).
// 2. Sun Microsystems (2025) 'Java I/O and File Handling'. Available at: https://docs.oracle.com/javase/tutorial/essential/io/ (Accessed: 14 April 2025).
// 3. Nimbus Look and Feel (2025) 'Using Nimbus Look and Feel in Java'. Available at: https://docs.oracle.com/javase/tutorial/uiswing/lookandfeel/nimbus.html (Accessed: 14 April 2025).
// 4. GitHub Contributors (2025) 'Java Chatbot Example'. Available at: https://github.com/example/java-chatbot (Accessed: 14 April 2025).