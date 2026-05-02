package org.jpgrammar;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.InputStream;
import java.util.*;

public class JMDictLoader {

    public static class Entry {
        public final String word;
        public final String kana;
        public final String romaji;
        public final List<String> writings;
        public final List<String> readings;
        public final List<String> meanings;
        public final List<String> partsOfSpeech;
        private final List<String> searchKeys;

        public Entry(String kana, String romaji, List<String> meanings) {
            this(kana, kana, romaji, List.of(), kana != null ? List.of(kana) : List.of(), meanings, List.of());
        }

        public Entry(String word, String kana, String romaji, List<String> writings,
                     List<String> readings, List<String> meanings, List<String> partsOfSpeech) {
            this.word = word;
            this.kana = kana;
            this.romaji = romaji;
            this.writings = List.copyOf(writings);
            this.readings = List.copyOf(readings);
            this.meanings = List.copyOf(meanings);
            this.partsOfSpeech = List.copyOf(partsOfSpeech);
            this.searchKeys = buildSearchKeys(word, this.writings, this.readings, romaji);
        }

        private static List<String> buildSearchKeys(String word, List<String> writings, List<String> readings, String romaji) {
            LinkedHashSet<String> keys = new LinkedHashSet<>();
            if (word != null && !word.isBlank()) {
                keys.add(word);
            }
            keys.addAll(writings);
            keys.addAll(readings);
            if (romaji != null && !romaji.isBlank()) {
                keys.add(romaji);
            }
            return keys.stream()
                    .filter(key -> key != null && !key.isBlank())
                    .map(key -> key.toLowerCase(Locale.ROOT))
                    .toList();
        }

        public boolean matchesExact(String query) {
            return searchKeys.stream().anyMatch(query::equals);
        }

        public boolean matchesPrefix(String query) {
            return searchKeys.stream().anyMatch(key -> key.startsWith(query));
        }

        public boolean matchesContains(String query) {
            return searchKeys.stream().anyMatch(key -> key.contains(query));
        }
    }

    private final Map<String, Entry> dict = new HashMap<>();
    private final List<Entry> entries = new ArrayList<>();

    public void load(String resourcePath) throws Exception {
// 通过类加载器读取 resources 目录中的 jmdict.xml
        InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath);
        if (is == null) {
            throw new IllegalArgumentException("资源未找到: " + resourcePath);
        }
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        // 设置实体展开数量上限（设置为无限大或较大值）
        setFactoryAttribute(factory, "http://www.oracle.com/xml/jaxp/properties/entityExpansionLimit", "1000000");
        setFactoryAttribute(factory, "http://www.oracle.com/xml/jaxp/properties/totalEntitySizeLimit", "0");
        setFactoryAttribute(factory, "jdk.xml.totalEntitySizeLimit", "0");
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(is);
        doc.getDocumentElement().normalize();

        NodeList entryList = doc.getElementsByTagName("entry");

        for (int i = 0; i < entryList.getLength(); i++) {
            Element entry = (Element) entryList.item(i);
            List<String> kebs = new ArrayList<>();
            List<String> rebs = new ArrayList<>();

            NodeList kebList = entry.getElementsByTagName("keb");
            for (int j = 0; j < kebList.getLength(); j++) {
                kebs.add(kebList.item(j).getTextContent());
            }

            NodeList rebList = entry.getElementsByTagName("reb");
            for (int j = 0; j < rebList.getLength(); j++) {
                rebs.add(rebList.item(j).getTextContent());
            }

            List<String> glosses = new ArrayList<>();
            NodeList glossList = entry.getElementsByTagName("gloss");
            for (int j = 0; j < glossList.getLength(); j++) {
                glosses.add(glossList.item(j).getTextContent());
            }

            List<String> partsOfSpeech = new ArrayList<>();
            NodeList posList = entry.getElementsByTagName("pos");
            for (int j = 0; j < posList.getLength(); j++) {
                partsOfSpeech.add(posList.item(j).getTextContent());
            }

            String kana = !rebs.isEmpty() ? rebs.get(0) : (!kebs.isEmpty() ? kebs.get(0) : null);
            String romaji = kana != null ? kanaToRomaji(kana) : "";
            String primaryWord = !kebs.isEmpty() ? kebs.get(0) : kana;

            Entry entryObj = new Entry(primaryWord, kana, romaji, kebs, rebs, glosses, partsOfSpeech);
            entries.add(entryObj);

            for (String writing : kebs) {
                dict.putIfAbsent(writing, entryObj);
            }
            for (String reading : rebs) {
                dict.putIfAbsent(reading, entryObj);
            }
        }
    }

    private void setFactoryAttribute(DocumentBuilderFactory factory, String name, String value) {
        try {
            factory.setAttribute(name, value);
        } catch (IllegalArgumentException e) {
            // 某些 JDK 不支持部分 JAXP 属性，忽略即可。
            System.err.println("JAXP 属性设置失败，忽略: " + name);
        }
    }

    public Entry getEntry(String word) {
        return dict.get(word);
    }

    public Entry findEntryFallback(String baseForm, String surface) {
        Entry entry = getEntry(baseForm);
        if (entry == null && surface != null && !surface.equals("*")) {
            entry = getEntry(surface);
        }
        return entry;
    }

    public List<Entry> search(String query, int limit) {
        if (query == null || query.isBlank()) {
            return List.of();
        }

        String normalizedQuery = query.trim().toLowerCase(Locale.ROOT);
        int safeLimit = Math.max(1, Math.min(limit, 50));
        LinkedHashSet<Entry> results = new LinkedHashSet<>();

        Entry exactEntry = getEntry(normalizedQuery);
        if (exactEntry != null) {
            results.add(exactEntry);
        }

        collectMatches(results, normalizedQuery, safeLimit, Entry::matchesExact);
        collectMatches(results, normalizedQuery, safeLimit, Entry::matchesPrefix);
        collectMatches(results, normalizedQuery, safeLimit, Entry::matchesContains);

        return results.stream().limit(safeLimit).toList();
    }

    private void collectMatches(Set<Entry> results, String query, int limit, EntryMatcher matcher) {
        if (results.size() >= limit) {
            return;
        }
        for (Entry entry : entries) {
            if (matcher.matches(entry, query)) {
                results.add(entry);
                if (results.size() >= limit) {
                    return;
                }
            }
        }
    }

    private interface EntryMatcher {
        boolean matches(Entry entry, String query);
    }

    private String kanaToRomaji(String kana) {
        if (kana == null) return "";

        kana = convertKatakanaToHiragana(kana); // 转换为平假名处理

        Map<String, String> map = new LinkedHashMap<>();
        // 拗音和特殊音
        map.put("きゃ", "kya"); map.put("きゅ", "kyu"); map.put("きょ", "kyo");
        map.put("しゃ", "sha"); map.put("しゅ", "shu"); map.put("しょ", "sho");
        map.put("ちゃ", "cha"); map.put("ちゅ", "chu"); map.put("ちょ", "cho");
        map.put("にゃ", "nya"); map.put("にゅ", "nyu"); map.put("にょ", "nyo");
        map.put("ひゃ", "hya"); map.put("ひゅ", "hyu"); map.put("ひょ", "hyo");
        map.put("みゃ", "mya"); map.put("みゅ", "myu"); map.put("みょ", "myo");
        map.put("りゃ", "rya"); map.put("りゅ", "ryu"); map.put("りょ", "ryo");

        // 特殊处理
        map.put("し", "shi"); map.put("ち", "chi"); map.put("つ", "tsu");
        map.put("ふ", "fu"); map.put("じ", "ji"); map.put("ぢ", "ji"); map.put("づ", "zu");

        // 单音
        String[] kanaArr = {
            "あ","い","う","え","お","か","き","く","け","こ","さ","し","す","せ","そ",
            "た","ち","つ","て","と","な","に","ぬ","ね","の","は","ひ","ふ","へ","ほ",
            "ま","み","む","め","も","や","ゆ","よ","ら","り","る","れ","ろ",
            "わ","を","ん","が","ぎ","ぐ","げ","ご","ざ","じ","ず","ぜ","ぞ",
            "だ","ぢ","づ","で","ど","ば","び","ぶ","べ","ぼ","ぱ","ぴ","ぷ","ぺ","ぽ"
        };
        String[] romaArr = {
            "a","i","u","e","o","ka","ki","ku","ke","ko","sa","shi","su","se","so",
            "ta","chi","tsu","te","to","na","ni","nu","ne","no","ha","hi","fu","he","ho",
            "ma","mi","mu","me","mo","ya","yu","yo","ra","ri","ru","re","ro",
            "wa","wo","n","ga","gi","gu","ge","go","za","ji","zu","ze","zo",
            "da","ji","zu","de","do","ba","bi","bu","be","bo","pa","pi","pu","pe","po"
        };
        for (int i = 0; i < kanaArr.length; i++) {
            map.put(kanaArr[i], romaArr[i]);
        }

        String result = kana;
        for (Map.Entry<String, String> e : map.entrySet()) {
            result = result.replace(e.getKey(), e.getValue());
        }

        return result;
    }

    private String convertKatakanaToHiragana(String input) {
        StringBuilder sb = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (c >= 'ァ' && c <= 'ン') {
                sb.append((char)(c - 0x60));  // カタカナ转为平假名
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
