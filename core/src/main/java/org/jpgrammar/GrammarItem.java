package org.jpgrammar;

import java.util.List;

public class GrammarItem {
    public String type;
    public String surface;
    public String baseForm;
    public String kana;
    public String romaji;
    public List<String> meanings;
    public String form; // 词形变化信息

    public GrammarItem(String type, String surface, String baseForm,
                       String kana, String romaji, List<String> meanings, String form) {
        this.type = type;
        this.surface = surface;
        this.baseForm = baseForm;
        this.kana = kana;
        this.romaji = romaji;
        this.meanings = meanings;
        this.form = form;
    }
}
