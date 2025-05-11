package org.jpgrammar;

import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;

import java.util.List;

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

    public static void analyze(String sentence) {
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
                String readingInfo = (entry != null && entry.kana != null) ?
                        " [" + entry.kana + "/" + entry.romaji + "]" : "";
                String meaning = (entry != null && !entry.meanings.isEmpty()) ?
                        " - " + String.join("; ", entry.meanings) : "";

                System.out.println("複合文法: " + grammar + readingInfo + meaning);
                i += match.length - 1; // 跳过匹配到的 token
                continue;
            }

            JMDictLoader.Entry entry = jmDict.findEntryFallback(baseForm, surface);
            String readingInfo = (entry != null && entry.kana != null) ?
                    " [" + entry.kana + "/" + entry.romaji + "]" : "";
            String meaning = (entry != null && !entry.meanings.isEmpty()) ?
                    " - " + String.join("; ", entry.meanings) : "";

            switch (pos) {
                case "助詞":
                case "助動詞":
                    System.out.println("助詞: " + surface + readingInfo + meaning);
                    break;
                case "動詞":
                    System.out.printf("動詞: %s (%s)%s%s%n",
                            baseForm,
                            conjugationForm != null ? conjugationForm : "基本形",
                            readingInfo,
                            meaning);
                    break;
                case "形容詞":
                    System.out.printf("形容詞: %s (%s)%s%s%n",
                            baseForm,
                            conjugationForm != null ? conjugationForm : "基本形",
                            readingInfo,
                            meaning);
                    break;
                default:
                    if (!pos.equals("記号")) {
                        System.out.println(pos + ": " + surface + readingInfo + meaning);
                    }
                    break;
            }
        }
    }

}
