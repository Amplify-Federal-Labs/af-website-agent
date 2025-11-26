package net.starkenberg.ai.springaiagent.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.starkenberg.ai.springaiagent.chat.Answer;
import net.starkenberg.ai.springaiagent.chat.Question;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAIService {
    private final ChatModel chatModel;
    private final VectorStore vectorStore;
    @Value("classpath:/templates/rag-prompt-template.st")
    private Resource ragPromptTemplate;

    public Answer answer(Question question) {
        // Use a non-empty template for SystemPromptTemplate
        List<Document> documents = vectorStore.similaritySearch(SearchRequest.builder().query(question.question()).topK(5).build());
        List<String> contentList = documents.stream().map(Document::getFormattedContent).toList();
        PromptTemplate promptTemplate = new PromptTemplate(ragPromptTemplate);
        Prompt prompt = promptTemplate.create(Map.of("input", question.question(), "documents", String.join("\n", contentList)));
        contentList.forEach(System.out::println);
        ChatResponse response = chatModel.call(prompt);
        return new Answer(response.getResult().getOutput().getText());
    }
}
