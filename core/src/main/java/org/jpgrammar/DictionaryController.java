package org.jpgrammar;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/dictionary")
public class DictionaryController {

    private final DictionaryService dictionaryService;

    public DictionaryController(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    @GetMapping("/search")
    public SearchResponse search(
            @RequestParam("q") String query,
            @RequestParam(value = "limit", defaultValue = "20") int limit
    ) {
        SearchResponse response = new SearchResponse();
        response.setQuery(query);
        response.setResults(dictionaryService.search(query, limit));
        return response;
    }

    public static class SearchResponse {
        private String query;
        private List<DictionaryEntryResponse> results;

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }

        public List<DictionaryEntryResponse> getResults() {
            return results;
        }

        public void setResults(List<DictionaryEntryResponse> results) {
            this.results = results;
        }
    }
}
