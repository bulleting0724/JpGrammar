package org.jpgrammar;
import com.atilika.kuromoji.ipadic.Token;
import java.util.*;


public class CompositeGrammarDetector {

    private static final List<String> compositePatterns = Arrays.asList(
            "ている", "ていく", "てきた", "なければならない", "そうだ", "ようだ", "らしい", "ことがある"
    );

    /**
     * 返回匹配的复合语法，包含：
     * - grammarText：合并后的表面形（如“ている”）
     * - length：匹配的 token 数量
     */
    public static MatchResult matchCompositeGrammar(List<Token> tokens, int startIndex) {
        StringBuilder sb = new StringBuilder();
        for (int i = startIndex; i < Math.min(tokens.size(), startIndex + 5); i++) {
            sb.append(tokens.get(i).getSurface());
            String current = sb.toString();
            if (compositePatterns.contains(current)) {
                return new MatchResult(current, i - startIndex + 1);
            }
        }
        return null;
    }

    public static class MatchResult {
        public final String grammarText;
        public final int length;

        public MatchResult(String grammarText, int length) {
            this.grammarText = grammarText;
            this.length = length;
        }
    }
}
