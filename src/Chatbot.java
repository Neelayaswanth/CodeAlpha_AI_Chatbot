import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Chatbot {
    private Map<String, String> responses;
    private Map<String, Integer> keywordFrequency;
    private List<String> conversationHistory;
    private Map<String, List<String>> contextResponses;
    private Set<String> stopWords;
    private Random random;
    private String botName = "CodeAlpha Bot";
    private String userContext = "";
    
    public Chatbot() {
        responses = new HashMap<>();
        keywordFrequency = new HashMap<>();
        conversationHistory = new ArrayList<>();
        contextResponses = new HashMap<>();
        stopWords = new HashSet<>();
        random = new Random();
        
        initializeStopWords();
        initializeResponses();
        initializeContextResponses();
        loadTrainingData();
    }
    
    private void initializeStopWords() {
        // Common English stop words for NLP processing
        String[] words = {"a", "an", "and", "are", "as", "at", "be", "by", "for", "from", 
                         "has", "he", "in", "is", "it", "its", "of", "on", "that", "the", 
                         "to", "was", "will", "with", "the", "this", "but", "they", "have", 
                         "had", "what", "said", "each", "which", "she", "do", "how", "their", 
                         "if", "up", "out", "many", "then", "them", "these", "so", "some", 
                         "her", "would", "make", "like", "into", "him", "time", "two", "more", 
                         "go", "no", "way", "could", "my", "than", "first", "been", "call", 
                         "who", "its", "now", "find", "long", "down", "day", "did", "get", 
                         "come", "made", "may", "part"};
        stopWords.addAll(Arrays.asList(words));
    }
    
    private void initializeResponses() {
        // Pre-populate the map with keywords and their corresponding responses
        responses.put("hello", "Hello there! How can I assist you today?");
        responses.put("hi", "Hi! Nice to meet you. How can I help?");
        responses.put("hey", "Hey! What can I do for you today?");
        responses.put("how are you", "I'm a bot, but I'm running perfectly! Thanks for asking.");
        responses.put("how are you doing", "I'm doing great! Ready to help you with anything.");
        responses.put("what's up", "Not much, just here to chat and help you out!");
        responses.put("name", "You can call me " + botName + ".");
        responses.put("what's your name", "My name is " + botName + ". Nice to meet you!");
        responses.put("who are you", "I'm " + botName + ", your friendly AI assistant.");
        responses.put("bye", "Goodbye! Have a great day.");
        responses.put("goodbye", "See you later! Take care!");
        responses.put("see you", "See you! Come back anytime!");
        responses.put("help", "You can ask me about my name, how I am, or just say hello. I'll do my best to answer!");
        responses.put("what can you do", "I can chat with you, answer basic questions, and help you with simple tasks. Just ask me anything!");
        responses.put("thanks", "You're welcome! Is there anything else I can help you with?");
        responses.put("thank you", "You're very welcome! Feel free to ask me more questions.");
        responses.put("weather", "I'm sorry, I don't have access to real-time weather data. You might want to check a weather app or website.");
        responses.put("time", "I don't have access to real-time clock data, but I hope you're having a great day!");
        responses.put("joke", "Why don't scientists trust atoms? Because they make up everything! 😄");
        responses.put("funny", "I try my best to be entertaining! Did you know that programmers prefer dark mode because light attracts bugs?");
        responses.put("love", "That's very kind of you! I'm designed to be helpful and friendly.");
        responses.put("hate", "I'm sorry to hear that. I'm here to help and make our conversation pleasant.");
        responses.put("sorry", "No worries at all! We all make mistakes. How can I help you?");
        responses.put("okay", "Great! What would you like to talk about?");
        responses.put("yes", "Excellent! What can I help you with?");
        responses.put("no", "No problem! Let me know if you change your mind.");
        
        // Add more sophisticated responses
        responses.put("programming", "I love programming! What language are you working with?");
        responses.put("java", "Java is a great programming language! It's object-oriented and platform-independent.");
        responses.put("python", "Python is excellent for beginners and has great libraries for AI and data science!");
        responses.put("javascript", "JavaScript is perfect for web development and creating interactive websites!");
        responses.put("algorithm", "Algorithms are fascinating! They're like recipes for solving problems efficiently.");
        responses.put("database", "Databases are essential for storing and managing data. SQL is a great place to start!");
        responses.put("machine learning", "Machine learning is amazing! It allows computers to learn from data and make predictions.");
        responses.put("artificial intelligence", "AI is transforming our world! It includes machine learning, natural language processing, and more.");
        responses.put("neural network", "Neural networks are inspired by the human brain and are great for pattern recognition!");
        responses.put("data science", "Data science combines statistics, programming, and domain knowledge to extract insights from data.");
    }
    
    private void initializeContextResponses() {
        // Context-aware responses based on conversation history
        contextResponses.put("greeting", Arrays.asList(
            "Hello! How can I help you today?",
            "Hi there! What would you like to know?",
            "Hey! I'm here to assist you with any questions."
        ));
        
        contextResponses.put("programming_help", Arrays.asList(
            "I'd be happy to help with programming! What specific issue are you facing?",
            "Programming can be challenging. Let me know what you're working on!",
            "I love helping with code! What language or problem are you dealing with?"
        ));
        
        contextResponses.put("learning", Arrays.asList(
            "Learning is a wonderful journey! What topic interests you most?",
            "I'm always learning too! What would you like to explore?",
            "Knowledge is power! What area would you like to dive into?"
        ));
    }
    
    private void loadTrainingData() {
        // Load additional training data from file if available
        try {
            Path trainingFile = Paths.get("training_data.txt");
            if (Files.exists(trainingFile)) {
                List<String> lines = Files.readAllLines(trainingFile);
                for (String line : lines) {
                    String[] parts = line.split("\\|");
                    if (parts.length == 2) {
                        responses.put(parts[0].toLowerCase().trim(), parts[1].trim());
                    }
                }
            }
        } catch (IOException e) {
            // Training file not found, continue with default responses
        }
    }
    
    public void trainBot(String question, String answer) {
        // Add new training data
        responses.put(question.toLowerCase().trim(), answer.trim());
        
        // Save to training file
        try {
            Files.write(Paths.get("training_data.txt"), 
                       (question.toLowerCase().trim() + "|" + answer.trim() + "\n").getBytes(),
                       StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Could not save training data: " + e.getMessage());
        }
    }
    
    public String getResponse(String userInput) {
        if (userInput == null || userInput.trim().isEmpty()) {
            return "Please say something! I'm here to chat with you.";
        }
        
        // Add to conversation history
        conversationHistory.add(userInput);
        if (conversationHistory.size() > 10) {
            conversationHistory.remove(0);
        }
        
        // NLP Processing
        String processedInput = preprocessInput(userInput);
        
        // Extract keywords and update frequency
        List<String> keywords = extractKeywords(processedInput);
        for (String keyword : keywords) {
            keywordFrequency.put(keyword, keywordFrequency.getOrDefault(keyword, 0) + 1);
        }
        
        // Check for exact matches first
        String exactMatch = responses.get(processedInput);
        if (exactMatch != null) {
            return exactMatch;
        }
        
        // Check for keyword matches with context awareness
        String response = findBestResponse(processedInput, keywords);
        
        // Update user context
        updateContext(processedInput, keywords);
        
        return response;
    }
    
    private String preprocessInput(String input) {
        // NLP preprocessing: lowercase, remove punctuation, normalize whitespace
        String processed = input.toLowerCase().trim();
        processed = processed.replaceAll("[^a-zA-Z0-9\\s]", " ");
        processed = processed.replaceAll("\\s+", " ");
        return processed;
    }
    
    private List<String> extractKeywords(String input) {
        List<String> keywords = new ArrayList<>();
        String[] words = input.split("\\s+");
        
        for (String word : words) {
            if (!stopWords.contains(word) && word.length() > 2) {
                keywords.add(word);
            }
        }
        
        return keywords;
    }
    
    private String findBestResponse(String input, List<String> keywords) {
        // Check for keyword matches
        for (String keyword : keywords) {
            for (Map.Entry<String, String> entry : responses.entrySet()) {
                if (entry.getKey().contains(keyword) || keyword.contains(entry.getKey())) {
                    return entry.getValue();
                }
            }
        }
        
        // Check for partial matches
        for (Map.Entry<String, String> entry : responses.entrySet()) {
            if (input.contains(entry.getKey()) || entry.getKey().contains(input)) {
                return entry.getValue();
            }
        }
        
        // Use context-aware responses
        String contextResponse = getContextResponse();
        if (contextResponse != null) {
            return contextResponse;
        }
        
        // Machine learning inspired response based on frequency
        return generateSmartResponse(keywords);
    }
    
    private String getContextResponse() {
        if (userContext.isEmpty()) {
            return null;
        }
        
        List<String> contextList = contextResponses.get(userContext);
        if (contextList != null && !contextList.isEmpty()) {
            return contextList.get(random.nextInt(contextList.size()));
        }
        
        return null;
    }
    
    private void updateContext(String input, List<String> keywords) {
        // Update context based on keywords and conversation history
        if (keywords.contains("programming") || keywords.contains("code") || 
            keywords.contains("java") || keywords.contains("python")) {
            userContext = "programming_help";
        } else if (keywords.contains("learn") || keywords.contains("study") || 
                   keywords.contains("education")) {
            userContext = "learning";
        } else if (keywords.contains("hello") || keywords.contains("hi") || 
                   keywords.contains("hey")) {
            userContext = "greeting";
        } else {
            userContext = "";
        }
    }
    
    private String generateSmartResponse(List<String> keywords) {
        // Machine learning inspired response generation
        if (keywords.isEmpty()) {
            return "I'm sorry, I don't understand that. Please try asking in a different way or type 'help' to see what I can do!";
        }
        
        // Check if any keywords have been seen before
        String mostFrequentKeyword = "";
        int maxFreq = 0;
        
        for (String keyword : keywords) {
            int freq = keywordFrequency.getOrDefault(keyword, 0);
            if (freq > maxFreq) {
                maxFreq = freq;
                mostFrequentKeyword = keyword;
            }
        }
        
        if (maxFreq > 0) {
            return "I see you're interested in '" + mostFrequentKeyword + "'. " +
                   "Could you tell me more about what you'd like to know?";
        }
        
        // Generate contextual response based on conversation history
        if (conversationHistory.size() > 1) {
            String lastMessage = conversationHistory.get(conversationHistory.size() - 2);
            if (lastMessage.toLowerCase().contains("name")) {
                return "I'm " + botName + ". What would you like to know about me?";
            }
        }
        
        // Default fallback with learning suggestion
        return "I'm sorry, I don't understand that yet. You can teach me by saying 'train: [question] | [answer]' " +
               "or type 'help' to see what I can do!";
    }
    
    public void processTrainingCommand(String input) {
        // Handle training commands in format: "train: question | answer"
        if (input.toLowerCase().startsWith("train:")) {
            String trainingData = input.substring(6).trim();
            String[] parts = trainingData.split("\\|");
            if (parts.length == 2) {
                trainBot(parts[0].trim(), parts[1].trim());
                System.out.println("Training successful! I've learned: " + parts[0].trim());
            }
        }
    }
    
    public Map<String, Integer> getKeywordFrequency() {
        return new HashMap<>(keywordFrequency);
    }
    
    public List<String> getConversationHistory() {
        return new ArrayList<>(conversationHistory);
    }
    
    public void resetContext() {
        userContext = "";
        conversationHistory.clear();
    }
} 