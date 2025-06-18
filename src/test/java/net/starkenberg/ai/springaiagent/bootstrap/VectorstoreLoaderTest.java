package net.starkenberg.ai.springaiagent.bootstrap;

import net.starkenberg.ai.springaiagent.services.WebScraperService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VectorstoreLoaderTest {

    @Mock
    private WebScraperService webScraperService;

    @Mock
    private VectorStore vectorStore;

    private VectorstoreLoader vectorstoreLoader;

    @BeforeEach
    void setUp() {
        vectorstoreLoader = new VectorstoreLoader(webScraperService, vectorStore);
    }

    @Test
    void testRunWhenVectorStoreIsEmpty() throws Exception {
        // Arrange
        Set<String> urls = new HashSet<>();
        urls.add("https://example.com");

        // Mock the vectorStore to return empty list for similaritySearch
        when(vectorStore.similaritySearch("amplifyfederal")).thenReturn(Collections.emptyList());

        // Mock the webScraperService to return our test URLs
        when(webScraperService.crawlSite()).thenReturn(urls);

        // Act
        vectorstoreLoader.run();

        // Assert
        verify(vectorStore).similaritySearch("amplifyfederal");
        verify(webScraperService).crawlSite();
        // We can't verify the exact interactions with TikaDocumentReader and TokenTextSplitter
        // since they are created inside the method, but we can verify that vectorStore.add was called
        verify(vectorStore, times(1)).add(anyList());
    }

    @Test
    void testRunWhenVectorStoreHasData() throws Exception {
        // Arrange
        List<Document> existingDocs = Collections.singletonList(mock(Document.class));

        // Mock the vectorStore to return non-empty list for similaritySearch
        when(vectorStore.similaritySearch("amplifyfederal")).thenReturn(existingDocs);

        // Act
        vectorstoreLoader.run();

        // Assert
        verify(vectorStore).similaritySearch("amplifyfederal");
        verifyNoInteractions(webScraperService);
        // Verify that vectorStore.add was not called
        verify(vectorStore, never()).add(anyList());
    }
}
