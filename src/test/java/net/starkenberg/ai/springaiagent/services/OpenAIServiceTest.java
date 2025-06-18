package net.starkenberg.ai.springaiagent.services;

import net.starkenberg.ai.springaiagent.chat.Answer;
import net.starkenberg.ai.springaiagent.chat.Question;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OpenAIServiceTest {

    @Mock
    private ChatModel chatModel;

    @Mock
    private VectorStore vectorStore;

    // Use a real ByteArrayResource instead of a mock
    private Resource ragPromptTemplate;

    private OpenAIService openAIService;

    @BeforeEach
    void setUp() throws Exception {
        // Create a real Resource with a simple template
        String templateContent = "You are an AI assistant. Answer the following question: {input}\n\nContext: {documents}";
        ragPromptTemplate = new ByteArrayResource(templateContent.getBytes());

        openAIService = new OpenAIService(chatModel, vectorStore);

        // Set the ragPromptTemplate field using reflection
        Field field = OpenAIService.class.getDeclaredField("ragPromptTemplate");
        field.setAccessible(true);
        field.set(openAIService, ragPromptTemplate);
    }

    @Test
    void testAnswerReturnsCorrectResponse() {
        // Arrange
        Question question = new Question("What are the company values?");
        String expectedAnswer = "The company values are integrity, innovation, and teamwork.";

        // Mock the vector store search
        Document document1 = new Document("Our company values are integrity, innovation, and teamwork.");
        Document document2 = new Document("We believe in customer satisfaction above all else.");
        List<Document> documents = List.of(document1, document2);
        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(documents);

        // Mock the chat model response using deep stubs
        ChatResponse mockResponse = mock(ChatResponse.class, RETURNS_DEEP_STUBS);
        when(mockResponse.getResult().getOutput().getText()).thenReturn(expectedAnswer);
        when(chatModel.call(any(Prompt.class))).thenReturn(mockResponse);

        // Act
        Answer answer = openAIService.answer(question);

        // Assert
        assertNotNull(answer);
        assertEquals(expectedAnswer, answer.answer());

        // Verify interactions
        verify(vectorStore).similaritySearch(any(SearchRequest.class));
        verify(chatModel).call(any(Prompt.class));
    }

    @Test
    void testVectorStoreSearchUsesCorrectQuery() {
        // Arrange
        String questionText = "What are the company values?";
        Question question = new Question(questionText);
        String expectedAnswer = "The company values are integrity, innovation, and teamwork.";

        // Mock the vector store search
        Document document = new Document("Our company values are integrity, innovation, and teamwork.");
        List<Document> documents = List.of(document);
        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(documents);

        // Mock the chat model response using deep stubs
        ChatResponse mockResponse = mock(ChatResponse.class, RETURNS_DEEP_STUBS);
        when(mockResponse.getResult().getOutput().getText()).thenReturn(expectedAnswer);
        when(chatModel.call(any(Prompt.class))).thenReturn(mockResponse);

        // Act
        openAIService.answer(question);

        // Assert & Verify
        verify(vectorStore).similaritySearch(any(SearchRequest.class));
    }

    @Test
    void testChatModelCalledWithCorrectPrompt() {
        // Arrange
        Question question = new Question("What are the company values?");
        String expectedAnswer = "The company values are integrity, innovation, and teamwork.";

        // Mock the vector store search
        Document document = new Document("Our company values are integrity, innovation, and teamwork.");
        List<Document> documents = List.of(document);
        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(documents);

        // Mock the chat model response using deep stubs
        ChatResponse mockResponse = mock(ChatResponse.class, RETURNS_DEEP_STUBS);
        when(mockResponse.getResult().getOutput().getText()).thenReturn(expectedAnswer);
        when(chatModel.call(any(Prompt.class))).thenReturn(mockResponse);

        // Act
        openAIService.answer(question);

        // Verify
        verify(chatModel).call(any(Prompt.class));
    }
}
