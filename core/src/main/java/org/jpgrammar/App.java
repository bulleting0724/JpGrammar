package org.jpgrammar;


import edu.cmu.lti.jawjaw.db.*;
import edu.cmu.lti.jawjaw.pobj.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Hello world!
 *
 */
@Slf4j
public class App
{

    public static void main( String[] args )
    {
        log.info("d");
        log.info( "Hello World!" );
        App app = new App();
        log.info(Particles.NE);
        app.splitWords("まずは、洗うの。それで、きれいになったら、こうやって切るの。");
    }

    private static void run( final String word, final POS pos ) {
        // Access the Japanese WordNet DB and process the raw data
        List<Word> words = WordDAO.findWordsByLemmaAndPos(word, pos);
        List<Sense> senses = SenseDAO.findSensesByWordid( words.get(0).getWordid() );
        String synsetId = senses.get(0).getSynset();
        Synset synset = SynsetDAO.findSynsetBySynset( synsetId );
        SynsetDef synsetDef = SynsetDefDAO.findSynsetDefBySynsetAndLang(synsetId, Lang.eng);
        List<Synlink> synlinks = SynlinkDAO.findSynlinksBySynset( synsetId );
        // Showing the result
        log.info(String.valueOf(words.get(0)));
        log.info(String.valueOf(senses));
        log.info(String.valueOf(senses.get(0)));
        log.info(String.valueOf(synset));
        log.info(String.valueOf(synsetDef));
        log.info(String.valueOf(synlinks.get(0)));
    }


    public void splitWords(String jpSentence) {
        log.info("jap sentence is : {}", jpSentence);
        App.run( "買収", POS.v );
    }



}
