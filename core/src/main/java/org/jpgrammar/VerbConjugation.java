package org.jpgrammar;

/**
 * the verb form convert a inflection form to dictionary form
 */
public abstract class VerbConjugation {
    public String name;

    /**
     *
     * @return suffix to the verb
     */
    public abstract String suffix();

    public abstract boolean isFormMatched(String sentence);
}
