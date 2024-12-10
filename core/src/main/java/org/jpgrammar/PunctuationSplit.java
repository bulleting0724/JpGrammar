package org.jpgrammar;

public class PunctuationSplit implements SplitProcessor{
    @Override
    public String[] split(String sentence) {
        return sentence.split("、");
    }
}
