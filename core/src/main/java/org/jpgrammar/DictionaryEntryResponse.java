package org.jpgrammar;

import java.util.List;

public record DictionaryEntryResponse(
        String word,
        String reading,
        String romaji,
        List<String> writings,
        List<String> readings,
        List<String> meanings,
        List<String> partsOfSpeech
) {
    public static DictionaryEntryResponse from(JMDictLoader.Entry entry) {
        return new DictionaryEntryResponse(
                entry.word,
                entry.kana,
                entry.romaji,
                entry.writings,
                entry.readings,
                entry.meanings,
                entry.partsOfSpeech
        );
    }
}
