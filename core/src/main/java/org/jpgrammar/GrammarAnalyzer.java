package org.jpgrammar;

import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class GrammarAnalyzer {

    private final static JMDictLoader jmDict;
    static {
        jmDict = new JMDictLoader();
        try {
            jmDict.load("JMdict_e.xml");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<GrammarItem> analyze(String sentence) {
        log.info("Analyzing: {}", sentence);
        List<GrammarItem> results = new ArrayList<>();

        Tokenizer tokenizer = new Tokenizer();
        List<Token> tokens = tokenizer.tokenize(sentence);

        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            String surface = token.getSurface();
            String pos = token.getPartOfSpeechLevel1();
            String baseForm = token.getBaseForm();
            String conjugationForm = token.getConjugationForm();

            // 检查复合语法
            CompositeGrammarDetector.MatchResult match = CompositeGrammarDetector.matchCompositeGrammar(tokens, i);
            if (match != null) {
                String grammar = match.grammarText;
                JMDictLoader.Entry entry = jmDict.findEntryFallback(grammar, grammar);
                GrammarItem item = new GrammarItem("複合文法", grammar, grammar,
                        entry != null ? entry.kana : null,
                        entry != null ? entry.romaji : null,
                        entry != null ? entry.meanings : null, null);
                results.add(item);
                i += match.length - 1;
                continue;
            }

            JMDictLoader.Entry entry = jmDict.findEntryFallback(baseForm, surface);

            switch (pos) {
                case "助詞":
                case "助動詞":
                    // 如果是助动词 "た"，且前面是动词，则不单独输出
                    if ("た".equals(surface) && i > 0 && "動詞".equals(tokens.get(i - 1).getPartOfSpeechLevel1())) {
                        break; // 跳过输出
                    }
                    String contextMeaning = getContextualParticleMeaning(tokens, i);
                    results.add(new GrammarItem("助詞", surface, baseForm,
                            entry != null ? entry.kana : null,
                            entry != null ? entry.romaji : null,
                            contextMeaning != null ? List.of(contextMeaning)
                                                   : (entry != null ? entry.meanings : null),
                            null));
                    break;
                case "動詞":
                    results.add(new GrammarItem("動詞", surface, baseForm,
                            entry != null ? entry.kana : null,
                            entry != null ? entry.romaji : null,
                            entry != null ? entry.meanings : null,
                            conjugationForm));
                    break;
                case "形容詞":
                    results.add(new GrammarItem("形容詞", surface, baseForm,
                            entry != null ? entry.kana : null,
                            entry != null ? entry.romaji : null,
                            entry != null ? entry.meanings : null,
                            conjugationForm));
                    break;
                default:
                    if (!pos.equals("記号")) {
                        results.add(new GrammarItem(pos, surface, baseForm,
                                entry != null ? entry.kana : null,
                                entry != null ? entry.romaji : null,
                                entry != null ? entry.meanings : null,
                                null));
                    }
                    break;
            }
        }

        return results;
    }

    private static String getContextualParticleMeaning(List<Token> tokens, int index) {
        String particle = tokens.get(index).getSurface();
        String nextSurface = (index + 1 < tokens.size()) ? tokens.get(index + 1).getSurface() : "";
        String compound = particle + nextSurface;

        switch (compound) {
            case "には": return "for; in regard to";
            case "として": return "as (a role)";
            case "にとって": return "for (someone); from the perspective of";
            case "により":
            case "によって": return "due to; by means of";
            case "について": return "about; concerning";
            case "に対して": return "towards; against; in contrast to";
            case "を通して": return "through; via";
        }

        switch (particle) {
            case "に":
                if (index + 1 < tokens.size()) {
                    String nextPos = tokens.get(index + 1).getPartOfSpeechLevel1();
                    if ("動詞".equals(nextPos)) {
                        return "to (destination); toward";
                    }
                }
                return "location/time marker; target of action";
            case "は": return "topic marker";
            case "を": return "direct object marker";
            case "で": return "means/location marker";
            case "が": return "subject marker";
            default: return null;
        }
    }
}
