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
        for (Token token : tokenizer.tokenize(sentence)) {
            String surface = token.getSurface();
            String pos = token.getPartOfSpeechLevel1();
            String baseForm = token.getBaseForm();
            String conjugationForm = token.getConjugationForm();
            List<String> meanings = jmDict.getMeanings(baseForm);

            String meaning = meanings.isEmpty() ? "" : " - " + String.join("; ", meanings);

            switch (pos) {
                case "助詞":
                case "助動詞":
                    System.out.println("助詞: " + surface + meaning);
                    break;
                case "動詞":
                    System.out.printf("動詞: %s (%s)%s%n", baseForm,
                        conjugationForm != null ? conjugationForm : "基本形", meaning);
                    break;
                case "形容詞":
                    System.out.printf("形容詞: %s (%s)%s%n", baseForm,
                        conjugationForm != null ? conjugationForm : "基本形", meaning);
                    break;
                default:
                    if (!pos.equals("記号")) {
                        System.out.println(pos + ": " + surface + meaning);
                    }
                    break;
            }
        }
    }

}
