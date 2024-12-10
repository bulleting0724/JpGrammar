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
    TO("と"),
    NONI("のに"),
    KEDO("けど"),
    KARA("から"),
    KA("か"),
    HI("へ"),
    MADE("まで"),
    MO("も"),
    YA("や"),
    YONE("よね"),
    YORI("より"),
    SHI("し"),
    NA("な"),
    KURAI("くらい"),
    GURAI("ぐらい"),
    DAKE("だけ"),
    NARA("なら"),
    TODE("ので"),
    DESU("です");


    public final String kana;
    Particles(String kana) {
        this.kana = kana;
    }


}
