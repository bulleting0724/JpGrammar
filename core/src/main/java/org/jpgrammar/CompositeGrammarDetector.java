package org.jpgrammar;

import com.atilika.kuromoji.ipadic.Token;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Pattern;

public class CompositeGrammarDetector {

    private static final List<GrammarRule> grammarRules = new ArrayList<>();

    static {
        try {
            loadGrammarRules("composite_grammar.txt");
        } catch (Exception e) {
            System.err.println("加载复合语法规则失败: " + e.getMessage());
        }
    }

    public static class GrammarRule {
        public final String pattern;
        public final Pattern regex;

        public GrammarRule(String pattern) {
            this.pattern = pattern;
            // 支持 * 通配符：て* → て.*
            this.regex = Pattern.compile("^" + pattern.replace("*", ".*") + "$");
        }

        public boolean matches(String text) {
            return regex.matcher(text).matches();
        }
    }

    private static void loadGrammarRules(String fileName) throws Exception {
        InputStream is = CompositeGrammarDetector.class.getClassLoader().getResourceAsStream(fileName);
        if (is == null) {
            throw new IllegalArgumentException("找不到 composite grammar 配置文件: " + fileName);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    grammarRules.add(new GrammarRule(line));
                }
            }
        }
    }

    public static MatchResult matchCompositeGrammar(List<Token> tokens, int startIndex) {
        StringBuilder sb = new StringBuilder();
        for (int i = startIndex; i < Math.min(tokens.size(), startIndex + 6); i++) {
            sb.append(tokens.get(i).getSurface());
            String candidate = sb.toString();
            for (GrammarRule rule : grammarRules) {
                if (rule.matches(candidate)) {
                    return new MatchResult(candidate, i - startIndex + 1);
                }
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
