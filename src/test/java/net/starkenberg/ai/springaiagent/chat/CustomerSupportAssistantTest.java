package net.starkenberg.ai.springaiagent.chat;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CustomerSupportAssistantTest {

    @Autowired
    private CustomerSupportAssistant customerSupportAssistant;

    @Test
    void testCustomerSupportAssistantExists() {
        // Verify that the CustomerSupportAssistant was autowired correctly
        assertNotNull(customerSupportAssistant);
    }

    @Test
    void testChatDoesNotThrowException() {
        // Arrange
        String chatId = "test-chat-id";
        String userMessage = "What are Amplify Federal's core values?";

        // Act & Assert
        assertDoesNotThrow(() -> {
            customerSupportAssistant.chat(chatId, userMessage);
        });
    }

    @Test
    void testChatWithEmptyMessageThrowsException() {
        // Arrange
        String chatId = "test-chat-id";
        String userMessage = "";

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            customerSupportAssistant.chat(chatId, userMessage);
        });

        // Verify the exception message
        assertTrue(exception.getMessage().contains("text cannot be null or empty"));
    }

    @Test
    void testChatWithNullChatIdThrowsException() {
        // Arrange
        String chatId = null;
        String userMessage = "What are Amplify Federal's core values?";

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            customerSupportAssistant.chat(chatId, userMessage);
        });

        // Verify the exception message
        assertTrue(exception.getMessage().contains("value cannot be null"));
    }
}
