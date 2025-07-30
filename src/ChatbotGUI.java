import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Map;
import java.util.List;

public class ChatbotGUI extends JFrame {
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private JButton trainButton;
    private JButton statsButton;
    private JButton clearButton;
    private Chatbot chatbot;
    private JScrollPane scrollPane;
    private JPanel controlPanel;
    private JLabel statusLabel;
    private JDialog trainingDialog;
    private JDialog statsDialog;
    
    public ChatbotGUI() {
        // Initialize the chatbot
        chatbot = new Chatbot();
        
        // Set up the frame
        setTitle("Advanced AI Chatbot - CodeAlpha Bot");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 600);
        setLocationRelativeTo(null); // Center the window on screen
        
        // Create components
        createComponents();
        
        // Set up layout
        setupLayout();
        
        // Add event listeners
        setupEventListeners();
        
        // Add welcome message
        displayWelcomeMessage();
        
        // Create dialogs
        createTrainingDialog();
        createStatsDialog();
    }
    
    private void createComponents() {
        // Create chat area with enhanced styling
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        chatArea.setBackground(new Color(248, 249, 250));
        chatArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create scroll pane for chat area
        scrollPane = new JScrollPane(chatArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Chat History"));
        
        // Create input field
        inputField = new JTextField();
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        inputField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        // Create buttons with modern styling and better contrast
        sendButton = createStyledButton("Send", new Color(41, 128, 185));
        trainButton = createStyledButton("Train Bot", new Color(46, 204, 113));
        statsButton = createStyledButton("Statistics", new Color(241, 196, 15));
        clearButton = createStyledButton("Clear Chat", new Color(231, 76, 60));
        
        // Create control panel
        controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        controlPanel.setBackground(new Color(240, 240, 240));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        // Create status label
        statusLabel = new JLabel("Ready to chat! Type 'help' for assistance.");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        statusLabel.setForeground(new Color(100, 100, 100));
    }
    
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(backgroundColor);
        
        // Use black text for all buttons for better visibility
        button.setForeground(new Color(0, 0, 0));
        
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Ensure button is opaque for better visibility
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        
        return button;
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Add chat area to center
        add(scrollPane, BorderLayout.CENTER);
        
        // Create bottom panel for input and controls
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Add input field and send button to bottom panel
        JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        
        bottomPanel.add(inputPanel, BorderLayout.CENTER);
        
        // Add control panel
        controlPanel.add(trainButton);
        controlPanel.add(statsButton);
        controlPanel.add(clearButton);
        controlPanel.add(statusLabel);
        
        bottomPanel.add(controlPanel, BorderLayout.SOUTH);
        
        // Add bottom panel to frame
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventListeners() {
        // Send button action listener
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        
        // Train button action listener
        trainButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showTrainingDialog();
            }
        });
        
        // Stats button action listener
        statsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showStatsDialog();
            }
        });
        
        // Clear button action listener
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearChat();
            }
        });
        
        // Input field key listener for Enter key
        inputField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }
            
            @Override
            public void keyReleased(KeyEvent e) {}
        });
    }
    
    private void displayWelcomeMessage() {
        chatArea.append("🤖 " + chatbot.getClass().getSimpleName() + ": Welcome! I'm your advanced AI assistant.\n");
        chatArea.append("💡 I can help you with programming, answer questions, and learn from our conversations.\n");
        chatArea.append("🎯 Try asking me about: Java, Python, Machine Learning, or just say hello!\n");
        chatArea.append("📚 You can also train me with new responses using the 'Train Bot' button.\n\n");
        chatArea.append("Type 'help' to see what I can do!\n\n");
    }
    
    private void sendMessage() {
        String userMessage = inputField.getText().trim();
        
        if (!userMessage.isEmpty()) {
            // Check for training command
            if (userMessage.toLowerCase().startsWith("train:")) {
                handleTrainingCommand(userMessage);
                inputField.setText("");
                return;
            }
            
            // Append user message to chat area
            chatArea.append("👤 You: " + userMessage + "\n");
            
            // Get bot response
            String botResponse = chatbot.getResponse(userMessage);
            
            // Append bot response to chat area
            chatArea.append("🤖 Bot: " + botResponse + "\n\n");
            
            // Clear input field
            inputField.setText("");
            
            // Scroll to bottom to show latest message
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
            
            // Update status
            updateStatus("Message sent successfully!");
        }
    }
    
    private void handleTrainingCommand(String message) {
        chatbot.processTrainingCommand(message);
        chatArea.append("🎓 Training: I've learned something new!\n\n");
        updateStatus("Training completed!");
    }
    
    private void createTrainingDialog() {
        trainingDialog = new JDialog(this, "Train the Bot", true);
        trainingDialog.setLayout(new BorderLayout());
        trainingDialog.setSize(500, 300);
        trainingDialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Question field
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Question:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        JTextField questionField = new JTextField(30);
        panel.add(questionField, gbc);
        
        // Answer field
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Answer:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        JTextArea answerArea = new JTextArea(5, 30);
        answerArea.setLineWrap(true);
        answerArea.setWrapStyleWord(true);
        JScrollPane answerScroll = new JScrollPane(answerArea);
        panel.add(answerScroll, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton trainButton = createStyledButton("Train", new Color(46, 204, 113));
        JButton cancelButton = createStyledButton("Cancel", new Color(149, 165, 166));
        
        trainButton.addActionListener(e -> {
            String question = questionField.getText().trim();
            String answer = answerArea.getText().trim();
            if (!question.isEmpty() && !answer.isEmpty()) {
                chatbot.trainBot(question, answer);
                chatArea.append("🎓 Training: I've learned: '" + question + "'\n\n");
                updateStatus("Training completed!");
                trainingDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(trainingDialog, "Please enter both question and answer!");
            }
        });
        
        cancelButton.addActionListener(e -> trainingDialog.dispose());
        
        buttonPanel.add(trainButton);
        buttonPanel.add(cancelButton);
        
        trainingDialog.add(panel, BorderLayout.CENTER);
        trainingDialog.add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void createStatsDialog() {
        statsDialog = new JDialog(this, "Chat Statistics", true);
        statsDialog.setLayout(new BorderLayout());
        statsDialog.setSize(400, 300);
        statsDialog.setLocationRelativeTo(this);
        
        JTextArea statsArea = new JTextArea();
        statsArea.setEditable(false);
        statsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane statsScroll = new JScrollPane(statsArea);
        statsDialog.add(statsScroll, BorderLayout.CENTER);
        
        JButton closeButton = createStyledButton("Close", new Color(149, 165, 166));
        closeButton.addActionListener(e -> statsDialog.dispose());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        statsDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        // Store reference to update stats
        statsDialog.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                updateStatsDisplay(statsArea);
            }
        });
    }
    
    private void updateStatsDisplay(JTextArea statsArea) {
        Map<String, Integer> keywordFreq = chatbot.getKeywordFrequency();
        List<String> history = chatbot.getConversationHistory();
        
        StringBuilder stats = new StringBuilder();
        stats.append("=== CHAT STATISTICS ===\n\n");
        stats.append("Total Messages: ").append(history.size()).append("\n");
        stats.append("Unique Keywords: ").append(keywordFreq.size()).append("\n\n");
        
        stats.append("=== TOP KEYWORDS ===\n");
        keywordFreq.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(10)
            .forEach(entry -> stats.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n"));
        
        stats.append("\n=== RECENT CONVERSATION ===\n");
        int start = Math.max(0, history.size() - 5);
        for (int i = start; i < history.size(); i++) {
            stats.append((i + 1)).append(". ").append(history.get(i)).append("\n");
        }
        
        statsArea.setText(stats.toString());
    }
    
    private void showTrainingDialog() {
        trainingDialog.setVisible(true);
    }
    
    private void showStatsDialog() {
        statsDialog.setVisible(true);
    }
    
    private void clearChat() {
        int choice = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to clear the chat history?", 
            "Clear Chat", JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            chatArea.setText("");
            chatbot.resetContext();
            displayWelcomeMessage();
            updateStatus("Chat cleared!");
        }
    }
    
    private void updateStatus(String message) {
        statusLabel.setText(message);
        Timer timer = new Timer(3000, e -> statusLabel.setText("Ready to chat! Type 'help' for assistance."));
        timer.setRepeats(false);
        timer.start();
    }
    
    public static void main(String[] args) {
        // Set look and feel to system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // If system look and feel fails, use default
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        // Create and show GUI on Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ChatbotGUI gui = new ChatbotGUI();
                gui.setVisible(true);
            }
        });
    }
} 