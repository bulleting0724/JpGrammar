package org.jpgrammar;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

public class JmdictHandler extends DefaultHandler {
    private static final String ARTICLES = "articles";
    private static final String JMDICT = "JMdict";
    private static final String ENTRY = "entry";
    private static final String ENTSEQ = "ent_seq";
    private static final String RELE = "r_ele";
    private static final String SENSE = "sense";


    private static final String ARTICLE = "article";
    private static final String TITLE = "title";
    private static final String CONTENT = "content";

    private static final String REB = "reb";

    private JmDict website;

    private StringBuilder elementValue;

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (elementValue == null) {
            elementValue = new StringBuilder();
        } else {
            elementValue.append(ch, start, length);
        }
    }

    @Override
    public void startDocument() throws SAXException {
        website = new JmDict();
    }

    @Override
    public void startElement(String uri, String lName, String qName, Attributes attr) throws SAXException {
        Entry entry = null;
        switch (qName) {
            case JMDICT:
                website.setEntryList(new ArrayList<>());
            case ENTRY:
                entry = new Entry();
                website.getEntryList().add(entry);

            case RELE:
                if (entry != null) {
                    entry.setR_eleList(new ArrayList<>());
                    R_Ele rEle = new R_Ele();
                    entry.getR_eleList().add(rEle);
                }


            case ARTICLES:
                website.setArticleList(new ArrayList<>());
                break;
            case ARTICLE:
                website.getArticleList().add(new BaeldungArticle());
                break;
            case TITLE:
                elementValue = new StringBuilder();
                break;
            case CONTENT:
                elementValue = new StringBuilder();
                break;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        switch (qName) {
            case REB:
            case TITLE:
                latestArticle().setTitle(elementValue.toString());
                break;
            case CONTENT:
                latestArticle().setContent(elementValue.toString());
                break;
        }
    }
    private Entry latestEntry() {
        List<Entry> entryList = website.getEntryList();
        int lastEntryIndex = entryList.size() - 1;
        return entryList.get(lastEntryIndex);
    }

    private BaeldungArticle latestArticle() {
        List<BaeldungArticle> articleList = website.getArticleList();
        int latestArticleIndex = articleList.size() - 1;
        return articleList.get(latestArticleIndex);
    }

    public JmDict getWebsite() {
        return website;
    }
}
