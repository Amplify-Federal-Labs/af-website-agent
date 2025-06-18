package net.starkenberg.ai.springaiagent.controllers;

import net.starkenberg.ai.springaiagent.chat.Answer;
import net.starkenberg.ai.springaiagent.chat.CustomerSupportAssistant;
import net.starkenberg.ai.springaiagent.chat.Question;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@SuppressWarnings("deprecation")
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerSupportAssistant assistant;

    @BeforeEach
    void setUp() {
        // Setup common mocks
        when(assistant.chat(anyString(), anyString())).thenAnswer(invocation -> {
            String question = invocation.getArgument(1);
            if (question.equals("What are Amplify Federal's core values?")) {
                return "Amplify Federal's core values are integrity, innovation, and excellence.";
            } else if (question.equals("What services does Amplify Federal offer?")) {
                return "Amplify Federal offers various technology and consulting services.";
            }
            return "Default response";
        });
    }

    @Test
    void testGetGeneration() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/ai")
                .param("question", "What are Amplify Federal's core values?"))
                .andExpect(status().isOk())
                .andExpect(content().string("Amplify Federal's core values are integrity, innovation, and excellence."));
    }

    @Test
    void testGetGenerationWithDefaultQuestion() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/ai"))
                .andExpect(status().isOk())
                .andExpect(content().string("Amplify Federal's core values are integrity, innovation, and excellence."));
    }

    @Test
    void testPostAnswer() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/ai")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"question\":\"What services does Amplify Federal offer?\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").value("Amplify Federal offers various technology and consulting services."));
    }
}
