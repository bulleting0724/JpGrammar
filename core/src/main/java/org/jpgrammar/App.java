package org.jpgrammar;


import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

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
    }
}
