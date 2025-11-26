package net.starkenberg.ai.springaiagent.chat;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

@Service
public class CustomerSupportAssistant {
    private final ChatClient chatClient;

    // @formatter:off
    public CustomerSupportAssistant(ChatClient.Builder chatClientBuilder, VectorStore vectorStore, ChatMemory chatMemory) {
        this.chatClient = chatClientBuilder
                .defaultSystem("""
						You are a customer chat support agent for the Amplify Federal website.
						Assume all questions pertain to Amplify Federal,
						if Amplify Federal is not mentioned in the question, add it to the question.
						Respond in a friendly, helpful, and joyful manner.
						You are interacting with customers through an online chat system.
						If you can not retrieve the information requested from the documents provided
						, please just say "I am sorry, I can not find the information requested.
					""")
                .defaultAdvisors(
                    MessageChatMemoryAdvisor.builder(chatMemory).build(),
                    QuestionAnswerAdvisor.builder(vectorStore).build()
                )
                .build();
    }
    // @formatter:on

    public String chat(String chatId, String userMessage, Object... additionalTools) {
        return this.chatClient.prompt()
                .user(userMessage)
                .tools(additionalTools)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, chatId))
                .call()
                .content();
    }
}
