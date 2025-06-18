package net.starkenberg.ai.springaiagent.services;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class WebScraperServiceTest {

    private static final String BASE_URL = "https://example.com";
    private WebScraperService webScraperService;

    @BeforeEach
    void setUp() {
        webScraperService = new WebScraperService(BASE_URL);
    }

    @Test
    void testConstructor() {
        // Verify that the constructor sets the baseUrl correctly
        WebScraperService service = new WebScraperService("https://test.com");
        // We can't directly access the private field, but we can test the behavior
        // by calling crawlSite with a mocked Jsoup
        try (MockedStatic<Jsoup> jsoupMock = mockStatic(Jsoup.class)) {
            // Setup mock
            Connection connectionMock = mock(Connection.class);
            Document documentMock = mock(Document.class);
            Elements elementsMock = mock(Elements.class);
            
            jsoupMock.when(() -> Jsoup.connect("https://test.com")).thenReturn(connectionMock);
            when(connectionMock.get()).thenReturn(documentMock);
            when(documentMock.select("a[href]")).thenReturn(elementsMock);
            when(elementsMock.iterator()).thenReturn(java.util.Collections.emptyIterator());
            
            // Call the method
            Set<String> result = service.crawlSite();
            
            // Verify that Jsoup.connect was called with the correct URL
            jsoupMock.verify(() -> Jsoup.connect("https://test.com"));
            
            // Verify that the result contains the base URL
            assertTrue(result.contains("https://test.com"));
        } catch (IOException e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }

    @Test
    void testCrawlSiteBasic() throws IOException {
        // Test a basic crawl with a single page and no links
        try (MockedStatic<Jsoup> jsoupMock = mockStatic(Jsoup.class)) {
            // Setup mock
            Connection connectionMock = mock(Connection.class);
            Document documentMock = mock(Document.class);
            Elements elementsMock = mock(Elements.class);
            
            jsoupMock.when(() -> Jsoup.connect(BASE_URL)).thenReturn(connectionMock);
            when(connectionMock.get()).thenReturn(documentMock);
            when(documentMock.select("a[href]")).thenReturn(elementsMock);
            when(elementsMock.iterator()).thenReturn(java.util.Collections.emptyIterator());
            
            // Call the method
            Set<String> result = webScraperService.crawlSite();
            
            // Verify that Jsoup.connect was called with the correct URL
            jsoupMock.verify(() -> Jsoup.connect(BASE_URL));
            
            // Verify that the result contains only the base URL
            assertEquals(1, result.size());
            assertTrue(result.contains(BASE_URL));
        }
    }

    @Test
    void testCrawlSiteWithLinks() throws IOException {
        // Test crawling a page with links to other pages on the same domain
        try (MockedStatic<Jsoup> jsoupMock = mockStatic(Jsoup.class)) {
            // Setup mocks for the base URL
            Connection baseConnectionMock = mock(Connection.class);
            Document baseDocumentMock = mock(Document.class);
            Elements baseElementsMock = mock(Elements.class);
            
            // Setup mocks for the first link
            Connection link1ConnectionMock = mock(Connection.class);
            Document link1DocumentMock = mock(Document.class);
            Elements link1ElementsMock = mock(Elements.class);
            
            // Setup the base URL connection
            jsoupMock.when(() -> Jsoup.connect(BASE_URL)).thenReturn(baseConnectionMock);
            when(baseConnectionMock.get()).thenReturn(baseDocumentMock);
            when(baseDocumentMock.select("a[href]")).thenReturn(baseElementsMock);
            
            // Create a link element
            Element linkElement = mock(Element.class);
            when(linkElement.absUrl("href")).thenReturn("https://example.com/page1");
            
            // Setup the elements iterator to return our link
            when(baseElementsMock.iterator()).thenReturn(java.util.List.of(linkElement).iterator());
            
            // Setup the link1 connection
            jsoupMock.when(() -> Jsoup.connect("https://example.com/page1")).thenReturn(link1ConnectionMock);
            when(link1ConnectionMock.get()).thenReturn(link1DocumentMock);
            when(link1DocumentMock.select("a[href]")).thenReturn(link1ElementsMock);
            when(link1ElementsMock.iterator()).thenReturn(java.util.Collections.emptyIterator());
            
            // Call the method
            Set<String> result = webScraperService.crawlSite();
            
            // Verify that Jsoup.connect was called with the correct URLs
            jsoupMock.verify(() -> Jsoup.connect(BASE_URL));
            jsoupMock.verify(() -> Jsoup.connect("https://example.com/page1"));
            
            // Verify that the result contains both URLs
            assertEquals(2, result.size());
            assertTrue(result.contains(BASE_URL));
            assertTrue(result.contains("https://example.com/page1"));
        }
    }

    @Test
    void testCrawlSiteWithExternalLinks() throws IOException {
        // Test that external links are not followed
        try (MockedStatic<Jsoup> jsoupMock = mockStatic(Jsoup.class)) {
            // Setup mocks
            Connection connectionMock = mock(Connection.class);
            Document documentMock = mock(Document.class);
            Elements elementsMock = mock(Elements.class);
            
            jsoupMock.when(() -> Jsoup.connect(BASE_URL)).thenReturn(connectionMock);
            when(connectionMock.get()).thenReturn(documentMock);
            when(documentMock.select("a[href]")).thenReturn(elementsMock);
            
            // Create a link element to an external site
            Element linkElement = mock(Element.class);
            when(linkElement.absUrl("href")).thenReturn("https://external-site.com");
            
            // Setup the elements iterator to return our external link
            when(elementsMock.iterator()).thenReturn(java.util.List.of(linkElement).iterator());
            
            // Call the method
            Set<String> result = webScraperService.crawlSite();
            
            // Verify that Jsoup.connect was called only with the base URL
            jsoupMock.verify(() -> Jsoup.connect(BASE_URL));
            jsoupMock.verify(() -> Jsoup.connect("https://external-site.com"), never());
            
            // Verify that the result contains only the base URL
            assertEquals(1, result.size());
            assertTrue(result.contains(BASE_URL));
        }
    }

    @Test
    void testCrawlSiteWithPdfLinks() throws IOException {
        // Test that PDF links are added to the result but not crawled
        try (MockedStatic<Jsoup> jsoupMock = mockStatic(Jsoup.class)) {
            // Setup mocks
            Connection connectionMock = mock(Connection.class);
            Document documentMock = mock(Document.class);
            Elements elementsMock = mock(Elements.class);
            
            jsoupMock.when(() -> Jsoup.connect(BASE_URL)).thenReturn(connectionMock);
            when(connectionMock.get()).thenReturn(documentMock);
            when(documentMock.select("a[href]")).thenReturn(elementsMock);
            
            // Create a link element to a PDF
            Element linkElement = mock(Element.class);
            when(linkElement.absUrl("href")).thenReturn("https://example.com/document.pdf");
            
            // Setup the elements iterator to return our PDF link
            when(elementsMock.iterator()).thenReturn(java.util.List.of(linkElement).iterator());
            
            // Call the method
            Set<String> result = webScraperService.crawlSite();
            
            // Verify that Jsoup.connect was called only with the base URL
            jsoupMock.verify(() -> Jsoup.connect(BASE_URL));
            jsoupMock.verify(() -> Jsoup.connect("https://example.com/document.pdf"), never());
            
            // Verify that the result contains both URLs
            assertEquals(2, result.size());
            assertTrue(result.contains(BASE_URL));
            assertTrue(result.contains("https://example.com/document.pdf"));
        }
    }

    @Test
    void testCrawlSiteWithError() throws IOException {
        // Test handling of connection errors
        try (MockedStatic<Jsoup> jsoupMock = mockStatic(Jsoup.class)) {
            // Setup mock to throw an exception
            Connection connectionMock = mock(Connection.class);
            
            jsoupMock.when(() -> Jsoup.connect(BASE_URL)).thenReturn(connectionMock);
            when(connectionMock.get()).thenThrow(new IOException("Connection error"));
            
            // Call the method
            Set<String> result = webScraperService.crawlSite();
            
            // Verify that Jsoup.connect was called with the correct URL
            jsoupMock.verify(() -> Jsoup.connect(BASE_URL));
            
            // Verify that the result still contains the base URL even though the connection failed
            assertEquals(1, result.size());
            assertTrue(result.contains(BASE_URL));
        }
    }

    @Test
    void testStripFragmentMethod() throws Exception {
        // Test the private stripFragment method using reflection
        java.lang.reflect.Method stripFragmentMethod = WebScraperService.class.getDeclaredMethod("stripFragment", String.class);
        stripFragmentMethod.setAccessible(true);
        
        // Test with a URL that has a fragment
        String result1 = (String) stripFragmentMethod.invoke(webScraperService, "https://example.com/page#section");
        assertEquals("https://example.com/page", result1);
        
        // Test with a URL that has a query parameter
        String result2 = (String) stripFragmentMethod.invoke(webScraperService, "https://example.com/page?param=value");
        assertEquals("https://example.com/page?param=value", result2);
        
        // Test with a URL that has both query parameter and fragment
        String result3 = (String) stripFragmentMethod.invoke(webScraperService, "https://example.com/page?param=value#section");
        assertEquals("https://example.com/page?param=value", result3);
        
        // Test with a URL that ends with a slash
        String result4 = (String) stripFragmentMethod.invoke(webScraperService, "https://example.com/page/");
        assertEquals("https://example.com/page", result4);
    }
}