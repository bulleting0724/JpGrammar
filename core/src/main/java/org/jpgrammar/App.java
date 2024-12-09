package org.jpgrammar;


import edu.cmu.lti.jawjaw.JAWJAW;
import edu.cmu.lti.jawjaw.db.*;
import edu.cmu.lti.jawjaw.pobj.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

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
        log.info(Particles.NO.kana);
        String sentence = "お父さんが買ったのは、バナナです";
        log.info(Arrays.toString(sentence.split(Particles.GA.kana)));
        runSimple("買い", POS.n);
    }

    private static void runSimple( String word, POS pos ) {
        // Accessing Japanese WordNet from the façade class called JAWJAW
        Set<String> hypernyms = JAWJAW.findHypernyms(word, pos);
        Set<String> hyponyms = JAWJAW.findHyponyms(word, pos);
        Set<String> consequents = JAWJAW.findEntailments(word, pos);
        Set<String> translations = JAWJAW.findTranslations(word, pos);
        Set<String> definitions = JAWJAW.findDefinitions(word, pos);
        // Showing results. (note: polysemies are mixed up here)
        System.out.println( "hypernyms of "+word+" : \t"+ hypernyms );
        System.out.println( "hyponyms of "+word+" : \t"+ hyponyms );
        System.out.println( word+" entails : \t\t"+ consequents );
        System.out.println( "translations of "+word+" : \t"+ translations );
        System.out.println( "definitions of "+word+" : \t"+ definitions );
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
}
