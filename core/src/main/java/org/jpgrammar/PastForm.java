package org.jpgrammar;

public class PastForm extends VerbConjugation{

    @Override
    public String suffix() {
        return "た";
    }

    @Override
    public boolean isFormMatched(String word) {
        return "た".equals(word);
    }



}
