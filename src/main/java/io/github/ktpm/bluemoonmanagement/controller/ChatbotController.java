package io.github.ktpm.bluemoonmanagement.controller;

import io.github.ktpm.bluemoonmanagement.service.chatbot.ChatBotService;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
public class ChatbotController {

    @FXML
    private TextArea chatArea;
    @FXML
    private TextField inputField;

    private ChatBotService chatBotService;

    @FXML
    public void initialize() {
        chatBotService = new ChatBotService();
        chatBotService.loadIntents("src/main/resources/intents.json");
    }

    @FXML
    public void handleSend() {
        String message = inputField.getText();
        if (message.isEmpty()) return;

        chatArea.appendText("Bạn: " + message + "\n");
        String response = chatBotService.getResponse(message);
        chatArea.appendText("Bot: " + response + "\n");

        inputField.clear();
    }
}
