package net.starkenberg.ai.springaiagent.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
        String systemPrompt = """
                You are an AI assistant specialized in analyzing Broadcom's quarterly financial reports. Your primary task is to answer questions accurately and precisely using the vector database, which contains relevant documents (2024 and 2025 quarterly reports).
                
                Only provide information that you retrieve from the documents (or verified expert knowledge). If something is not included in the dataset or is unclear, clearly state that you do not have sufficient information.
                
                Structure of your responses:
                • Concise and to the point
                • Specific numbers and facts, when available
                • Clearly indicate which quarterly report the information comes from
                
                Objective:
                Provide users with reliable and quick insights into Broadcom's quarterly financials without unnecessary details.""".stripIndent();
        return chatClientBuilder
                .defaultSystem(systemPrompt)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(MessageWindowChatMemory.builder().build()).build()
                      , RetrievalAugmentationAdvisor.builder().build()
                ).build();
    }
}
