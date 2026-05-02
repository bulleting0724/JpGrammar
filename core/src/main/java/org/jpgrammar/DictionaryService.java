package org.jpgrammar;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DictionaryService {

    public List<DictionaryEntryResponse> search(String query, int limit) {
        return GrammarAnalyzer.dictionary().search(query, limit)
                .stream()
                .map(DictionaryEntryResponse::from)
                .toList();
    }
}
