package net.starkenberg.ai.springaiagent.bootstrap;


import lombok.extern.slf4j.Slf4j;
import net.starkenberg.ai.springaiagent.services.WebScraperService;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class VectorstoreLoader implements CommandLineRunner {

    private final WebScraperService webScraperService;
    private final VectorStore vectorStore;

    public VectorstoreLoader(WebScraperService webScraperService, VectorStore vectorStore) {
        this.webScraperService = webScraperService;
        this.vectorStore = vectorStore;
    }

    @Override
    public void run(String... args) throws Exception {
        if(vectorStore.similaritySearch("amplifyfederal").isEmpty()) {
            webScraperService.crawlSite().forEach(site -> {
                log.debug("Loading vector for {}", site);
                TikaDocumentReader reader = new TikaDocumentReader(site);
                List<Document> docs = reader.get();
                TextSplitter splitter = new TokenTextSplitter();
                List<Document> splitDocs = splitter.apply(docs);
                vectorStore.add(splitDocs);
            });
            log.info("Vectorstore loaded");
        }
    }
}
