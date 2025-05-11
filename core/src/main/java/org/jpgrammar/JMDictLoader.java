package org.jpgrammar;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.InputStream;
import java.util.*;

public class JMDictLoader {

    public static class Entry {
        public final String kana;
        public final String romaji;
        public final List<String> meanings;

        public Entry(String kana, String romaji, List<String> meanings) {
            this.kana = kana;
            this.romaji = romaji;
            this.meanings = meanings;
        }
    }

    private final Map<String, Entry> dict = new HashMap<>();

    public void load(String resourcePath) throws Exception {
// 通过类加载器读取 resources 目录中的 jmdict.xml
        InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath);
        if (is == null) {
            throw new IllegalArgumentException("资源未找到: " + resourcePath);
        }
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        // 设置实体展开数量上限（设置为无限大或较大值）
        try {
            factory.setAttribute("http://www.oracle.com/xml/jaxp/properties/entityExpansionLimit", "1000000");
        } catch (IllegalArgumentException e) {
            // 某些 JDK 不支持这个属性，不处理即可
            System.err.println("JAXP 安全属性设置失败，忽略。");
        }
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

            String kana = !rebs.isEmpty() ? rebs.get(0) : (!kebs.isEmpty() ? kebs.get(0) : null);
            String romaji = kana != null ? kanaToRomaji(kana) : "";

            Entry entryObj = new Entry(kana, romaji, glosses);

            for (String word : kebs) {
                dict.putIfAbsent(word, entryObj);
            }
            for (String word : rebs) {
                dict.putIfAbsent(word, entryObj);
            }
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
