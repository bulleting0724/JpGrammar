package org.jpgrammar;

public enum Particles {
    GA("が"),
    WA("は"),
    WO("を"),
    NI("に"),
    DE("で"),
    YO("よ"),
    NE("ね"),
    NO("の"),
    TO("と");


    public String kana;
    Particles(String kana) {
        this.kana = kana;
    }


}
