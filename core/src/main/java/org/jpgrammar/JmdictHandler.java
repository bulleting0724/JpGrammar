package org.jpgrammar;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

public class JmdictHandler extends DefaultHandler {
    private static final String JMDICT = "JMdict";
    private static final String ENTRY = "entry";
    private static final String ENTSEQ = "ent_seq";
    private static final String RELE = "r_ele";

    private static final String KELE = "k_ele";
    private static final String SENSE = "sense";

    private static final String REB = "reb";
    private static final String KEB = "keb";

    private static final String RERESTR = "re_restr";
    private static final String REPRI = "re_pri";
    private static final String KEPRI = "ke_pri";

    private static final String GLOSS = "gloss";
    private static final String POS = "pos";

    private JmDict website;

    private StringBuilder elementValue;
    private Entry entry = null;
    private R_Ele rEle;
    private K_Ele kEle;

    private Sense sense;

    @Override
    public void characters(char[] ch, int start, int length) {
        if (elementValue == null) {
            elementValue = new StringBuilder();
        } else {
            elementValue.append(ch, start, length);
        }
    }

    @Override
    public void startDocument() {
        website = new JmDict();
    }

    @Override
    public void startElement(String uri, String lName, String qName, Attributes attr) {

        switch (qName) {
            case JMDICT:
                website.setEntryList(new ArrayList<>());
                break;
            case ENTRY:
                entry = new Entry();
                entry.setK_eleList(new ArrayList<>());
                entry.setR_eleList(new ArrayList<>());
                entry.setSenseList(new ArrayList<>());
                website.getEntryList().add(entry);
                break;
            case ENTSEQ:
            case RERESTR:
            case REPRI:
            case REB:
            case GLOSS:
            case POS:
            case KEB:
            case KEPRI:
                elementValue = new StringBuilder();
                break;
            case KELE:
                Entry latestEntry = latestEntry();
                if (latestEntry != null) {
                    kEle = new K_Ele();
                    latestEntry.getK_eleList().add(kEle);
                }
                break;
            case SENSE:
                sense = new Sense();
                sense.setPos(new ArrayList<>());
                Entry latestEntry2 = latestEntry();
                if (latestEntry2 != null) {
                    latestEntry2.getSenseList().add(sense);
                }
                break;
            case RELE:
                Entry latestEntry1 = latestEntry();
                if (latestEntry1 != null) {
                    rEle = new R_Ele();
                    latestEntry1.getR_eleList().add(rEle);
                }
                break;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        switch (qName) {
            case ENTSEQ:
                entry.setEntrySeq(elementValue.toString());
                break;
            case REB:
                rEle.setReb(elementValue.toString());
                break;
            case RERESTR:
                rEle.setReReStr(elementValue.toString());
                break;
            case REPRI:
                rEle.setRePri(elementValue.toString());
                break;
            case KEB:
                kEle.setKeb(elementValue.toString());
                break;
            case KEPRI:
                kEle.setKePri(elementValue.toString());
                break;
            case POS:
                sense.getPos().add(elementValue.toString());
                break;
            case GLOSS:
                sense.setGloss(elementValue.toString());
                break;
        }
    }
    private Entry latestEntry() {
        List<Entry> entryList = website.getEntryList();
        int lastEntryIndex = entryList.size() - 1;
        return entryList.get(lastEntryIndex);
    }


    public JmDict getWebsite() {
        return website;
    }
}
