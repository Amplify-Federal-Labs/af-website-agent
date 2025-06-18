package net.starkenberg.ai.springaiagent.services;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

@Slf4j
@Service
public class WebScraperService {

    private final String baseUrl;

    public WebScraperService(@Value("${app.site.baseUrl}") String baseUrl) {
        this.baseUrl = baseUrl;
    }

    /**
     * Crawl the Amplify home page and all children to return
     * @return Set of pages and docs to load in the vector store
     */
    public Set<String> crawlSite() {
        Set<String> visited = new HashSet<>();
        Queue<String> toVisit = new LinkedList<>();
        URI baseUri = URI.create(baseUrl);
        toVisit.add(baseUrl);
        while (!toVisit.isEmpty()) {
            String currentUrl = toVisit.poll();
            // if adding to Set fails or if it's not a page that we can get more links from just continue
            if (!visited.add(currentUrl) || currentUrl.endsWith("pdf") || currentUrl.endsWith("docx")) continue;
            try {
                Document doc = Jsoup.connect(currentUrl).get();
                Elements links = doc.select("a[href]");
                for (Element link : links) {
                    String href = stripFragment(link.absUrl("href"));
                    URI uri = URI.create(href);

                    if (href.startsWith("https")
                            && uri.getHost().equalsIgnoreCase(baseUri.getHost())
                            && !visited.contains(href)) {
                        toVisit.add(href);
                    }
                }
            } catch (Exception e) {
                log.error("Failed to fetch: {} \n {}", currentUrl, e.getMessage());
            }
        }
        return visited;
    }

    private String stripFragment(String url) {
        try {
            URI uri = new URI(url);
            // ignore fragments (in page references) we don't need to index the same page for each reference
            return new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), uri.getQuery(), null).toString().replaceAll("/$", "");
        } catch (Exception e) {
            log.error("Error stripping: {} \n {}", url, e.getMessage());
            return url; // Fallback if malformed
        }
    }
}