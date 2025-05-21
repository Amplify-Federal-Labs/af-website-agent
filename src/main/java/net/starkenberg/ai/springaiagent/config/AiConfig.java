package net.starkenberg.ai.springaiagent.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.DefaultChatClientBuilder;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.VectorStoreChatMemoryAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pinecone.PineconeVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    private final String pineconeApiKey;
    private final String pineconeIndexName;

    public AiConfig(@Value("${spring.ai.vectorstore.pinecone.api-key}")String pineconeApiKey
            , @Value("${spring.ai.vectorstore.pinecone.index-name}")String pineconeIndexName) {
        this.pineconeApiKey = pineconeApiKey;
        this.pineconeIndexName = pineconeIndexName;
    }

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
//                       , QuestionAnswerAdvisor.builder(vectorStore).build()
                ).build();
    }

//    @Bean
//    public VectorStore vectorStore(EmbeddingModel embeddingModel) {
//        return PineconeVectorStore.builder(embeddingModel)
//                .apiKey(pineconeApiKey).indexName(pineconeIndexName)
//                .namespace("avgo").build();
//    }

    @Bean
    public Advisor retrievalAugmentationAdvisor() {
        RetrievalAugmentationAdvisor.builder()
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .similarityThreshold(0.50)
                        .vectorStore(vectorStore)
                        .build())
                .build();
    }
}
