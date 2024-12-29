package org.jpgrammar;


import edu.cmu.lti.jawjaw.JAWJAW;
import edu.cmu.lti.jawjaw.pobj.POS;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
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
}
