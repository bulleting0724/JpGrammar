package org.jpgrammar;

public class PastForm extends VerbConjugation{

    @Override
    public String suffix() {
        return "た";
    }

    @Override
    public boolean isFormMatched(String word) {
        if ("た".equals(word)) {
            return true;
        }
        return false;
    }



}
